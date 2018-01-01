package ru.mipt.java2017.hw2;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.mipt.java2017.hw2.WorkerGrpc.WorkerFutureStub;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static java.lang.Math.min;
import static java.lang.Thread.sleep;

public class Client {

    private static final Logger logger = LoggerFactory.getLogger("Client");
    private static Lock locker = new ReentrantLock();
    
    private static ArrayList<Pair<String, Integer>> hosts_ports;
    private static ArrayList<Pair<ManagedChannel, WorkerFutureStub>> channels_futures;
    private static HashSet<Pair<Long, Long>> to_do;

    private static long answer;

    private static void createChannelsFutures() {
        for (Pair<String, Integer> host : hosts_ports) {
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress(host.getKey(), host.getValue())
                    .usePlaintext(true)
                    .build();
            if (channel != null) {
                channels_futures.add(new Pair<>(channel, WorkerGrpc.newFutureStub(channel)));
            }
        }
    }

    private static void sendRequestToServer(SumRequest request,
                                     final WorkerGrpc.WorkerFutureStub futureStub,
                                     ManagedChannel channel) {
        ListenableFuture<SumReply> response = futureStub.calculateSum(request);
        locker.lock();
        to_do.add(new Pair<>(request.getStart(), request.getEnd()));
        locker.unlock();

        Futures.addCallback(response,
                new FutureCallback<SumReply>() {
                    @Override
                    public void onSuccess(@Nullable SumReply result) {
                        if (result == null) {
                            logger.warn("result " + request.getStart() + " " + request.getEnd() + " can't come");
                        } else {
                            logger.info("Get sum from {} to {}", request.getStart(), request.getEnd());
                            locker.lock();
                            answer += result.getSum();
                            to_do.remove(new Pair<>(request.getStart(), request.getEnd()));
                            locker.unlock();
                        }
                    }
                    @Override
                    public void onFailure(@Nullable Throwable t) {
                        channels_futures.remove(new Pair<>(channel, futureStub));
                        if (t != null) {
                            logger.warn(request.getStart() + " " + request.getEnd() + " can't compute " + t.getMessage());
                        } else {
                            logger.warn(request.getStart() + " " + request.getEnd() + " can't compute.");
                        }
                        locker.lock();
                        to_do.remove(new Pair<>(request.getStart(), request.getEnd()));
                        locker.unlock();
                        distribution(request.getStart(), request.getEnd());
                    }
                },
                directExecutor());
    }

    private static void distribution(long start, long end) {
        long interval = (end - start) / channels_futures.size();
        long current = start;
        for (Pair<ManagedChannel, WorkerFutureStub> iter: channels_futures) {
            SumRequest request = SumRequest.newBuilder()
                    .setStart(current)
                    .setEnd(min(current + interval, end))
                    .build();
            sendRequestToServer(request, iter.getValue(), iter.getKey());
            current = current + interval;
        }
    }

    private static void getAnswer(long from, long to) throws InterruptedException {
        createChannelsFutures();
        distribution(from, to);
        while (!to_do.isEmpty() && !channels_futures.isEmpty()) {
            try {
                sleep(10);
            } catch (InterruptedException e) {
                logger.warn("Sleep interrupted " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception{
        if (args.length < 2 || args.length % 2 != 0) {
            logger.error("Bad arguments");
            System.exit(1);
        }
        answer = 0;
        hosts_ports = new ArrayList<>();
        channels_futures = new ArrayList<>();
        to_do = new HashSet<>();
        for (int i = 2; i < args.length; i += 2) {
            String hostname = args[i];
            Integer port = Integer.parseInt(args[i + 1]);
            hosts_ports.add(new Pair<>(hostname, port));
        }
        try {
            long start = Long.parseLong(args[0]);
            long end = Long.parseLong(args[1]) + 1;
            getAnswer(start, end);
            logger.info("Sum succesfully calculated");
            System.out.println(answer);
        } catch (InterruptedException e) {
            logger.error("Interruption exception");
        }
    }
}