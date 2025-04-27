package com;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Process {
    private List<Process> processList;
    private Random random;
    int pid;
    int burstTime;
    int arrivalTime;
    int priority;

    // Default constructor
    public Process() {
        processList = new ArrayList<>();
        random = new Random();
    }

    public Process(int pid, int burstTime, int arrivalTime) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = 0;
    }

    public Process(int pid, int burstTime, int arrivalTime, int priority) {
        this.pid = pid;
        this.burstTime = burstTime;
        this.arrivalTime = arrivalTime;
        this.priority = priority;
    }

    // All processes arrive at time 0 with identical burst times.
    public static List<Process> generateIdenticalProcesses(int np) {
        List<Process> processList = new ArrayList<>();
        for (int i = 0; i < np; i++) {
            processList.add(new Process(i, 5, 0, 0));
        }

        return processList;
    }

    // Extremely long burst times mixed with very short burst times.
    public static List<Process> generateMixedBurstProcesses(int np) {
        List<Process> processList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < np; i++) {
            int burstTime = random.nextInt(50) + 1;
            processList.add(new Process(i, burstTime, 0, 0));
        }
        return processList;
    }

    // Random priorities that drastically skew scheduling outcomes.
    public static List<Process> generatePriorityProcesses(int np) {
        List<Process> processList = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < np; i++) {
            int burstTime = random.nextInt(20) + 1;
            int priority = random.nextInt(10) + 1;
            processList.add(new Process(i, burstTime, 0, priority));
        }
        return processList;
    }


}