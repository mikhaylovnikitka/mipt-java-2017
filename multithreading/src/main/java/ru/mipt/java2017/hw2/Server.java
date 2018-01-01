package ru.mipt.java2017.hw2;

import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;

public class Server {

    private static final Logger logger = LoggerFactory.getLogger("Server");

    private static io.grpc.Server server;

    private static int threads_count;
    private static int port_number;

    private static void start() throws IOException {
        server = ServerBuilder.forPort(port_number)
                .addService(new WorkerImpl())
                .build()
                .start();
        logger.info("Server started, listening on " + port_number);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                Server.stop();
                System.err.println("*** server shut down");
            }
        });
    }

    private static void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    private static void blockUntilShutDown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length != 2) {
            logger.error("Bad arguments");
            System.exit(1);
        }
        threads_count = Integer.parseInt(args[0]);
        port_number = Integer.parseInt(args[1]);
        try {
            start();
            blockUntilShutDown();
        } catch (IOException | InterruptedException e) {
            stop();
        }
    }

    private static long calcSum(long from, long to) {
        ExecutorService executor = Executors.newFixedThreadPool(threads_count);
        ArrayList<Calculator> processes = new ArrayList<>();
        for (int i = 0; i < threads_count; ++i) {
            long a = from + i * (to - from) / threads_count;
            long b = from + (i + 1) * (to - from) / threads_count;
            logger.info("Created thread to calculate from {} to {}", a, b);
            processes.add(new Calculator(a, b));
        }
        long sum = 0;
        try {
            for (Future<Long> future : executor.invokeAll(processes)) {
                sum += future.get();
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted exception");
            return -1;
        } catch (ExecutionException e) {
            logger.error("Execution exception", e);
            return -1;
        }
        return sum;
    }

    private static class Calculator implements Callable<Long> {

        private long from, to;

        Calculator(long from, long to) {
            this.from = from;
            this.to = to;
        }

        private boolean isPrime(long n) {
            for (int i = 2; i * i <= n; ++i) {
                if (n % i == 0)
                    return false;
            }
            return n != 1;
        }

        @Override
        public Long call() {
            long sum = 0;
            for (long i = from; i < to; ++i) {
                if (isPrime(i))
                    sum += i;
            }
            logger.info("Sum from {} to {} is {}", from, to, sum);
            return sum;
        }
    }

    private static class WorkerImpl extends WorkerGrpc.WorkerImplBase{
        @Override
        public void calculateSum(SumRequest req, StreamObserver<SumReply> responseObserver) {
            logger.info("Got request from client: from {}, to {}", req.getStart(), req.getEnd());
            long sum = calcSum(req.getStart(), req.getEnd());
            logger.info("Sum from {} to {} calculated: {}", req.getStart(), req.getEnd(), sum);
            SumReply reply = SumReply.newBuilder().setSum(sum).build();
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}