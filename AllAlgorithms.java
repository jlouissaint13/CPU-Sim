package com;

import java.util.*;

public class AllAlgorithms {

    // FCFS First Come First Serve scheduling
    public static void fcfs(List<Process> processes) {
        // Sort processes by arrival time
        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int idleTime = 0;
        int totalBurstTime = 0;

        System.out.println("FCFS Scheduling");
        System.out.println("------------------------------------------------------------------------");
        System.out.println("Process ID  Arrival Time    Burst Time  Waiting Time    Turnaround Time");

        for (Process process : processes) {
            if (currentTime < process.arrivalTime) {
                // CPU idle time if next process hasn't arrived yet;
                idleTime += (process.arrivalTime - currentTime);
                currentTime = process.arrivalTime;
            }

            int waitingTime = currentTime - process.arrivalTime;
            int turnaroundTime = waitingTime + process.burstTime;

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;
            totalBurstTime += process.burstTime;

            System.out.println(process.pid + "\t\t" + process.arrivalTime + "\t\t" +
                    process.burstTime + "\t\t" + waitingTime + "\t\t" + turnaroundTime);

            currentTime += process.burstTime;
        }

        double avgWaitingTime = (double) totalWaitingTime / processes.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processes.size();
        double cpuUtilization = ((double)(currentTime - idleTime) / currentTime) * 100;
        double throughput = (double) processes.size() / currentTime;

        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
        System.out.println("CPU Utilization: " + String.format("%.2f%%", cpuUtilization));
        System.out.println("Throughput: " + String.format("%.4f processes/time unit", throughput));
        System.out.println("-------------------------------------------------------\n");
    }

    // SRTF Shortest Remaining Time First scheduling
    public static void srtf(List<Process> processes) {
        List<Process> processCopies = new ArrayList<>();
        for (Process p : processes) {
            processCopies.add(new Process(p.pid, p.burstTime, p.arrivalTime, p.priority));
        }

        // Sort processes by arrival time;
        processCopies.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalProcesses = processCopies.size();
        int completedProcesses = 0;
        int idleTime = 0;
        int totalBurstTime = 0;

        // Calculate total burst time;
        for (Process p : processCopies) {
            totalBurstTime += p.burstTime;
        }

        int[] waitingTime = new int[totalProcesses];
        int[] turnaroundTime = new int[totalProcesses];
        int[] remainingBurstTime = new int[totalProcesses];


        for (int i = 0; i < totalProcesses; i++) {
            remainingBurstTime[i] = processCopies.get(i).burstTime;
        }

        System.out.println("SRTF Scheduling");
        System.out.println("-------------------------------------------------------");

        StringBuilder timeline = new StringBuilder("Timeline: ");
        int prevProcess = -1;

        while (completedProcesses < totalProcesses) {
            int shortestJobIndex = -1;
            int shortestJobTime = Integer.MAX_VALUE;


            for (int i = 0; i < totalProcesses; i++) {
                if (processCopies.get(i).arrivalTime <= currentTime &&
                        remainingBurstTime[i] > 0 &&
                        remainingBurstTime[i] < shortestJobTime) {
                    shortestJobTime = remainingBurstTime[i];
                    shortestJobIndex = i;
                }
            }

            if (shortestJobIndex == -1) {

                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processCopies) {
                    if (p.arrivalTime > currentTime && p.arrivalTime < nextArrival) {
                        nextArrival = p.arrivalTime;
                    }
                }

                if (nextArrival != Integer.MAX_VALUE) {
                    if (prevProcess != -1) {
                        timeline.append("P").append(prevProcess).append("(").append(currentTime).append(") ");
                    }
                    timeline.append("IDLE(").append(nextArrival).append(") ");
                    idleTime += (nextArrival - currentTime);
                    prevProcess = -1;
                    currentTime = nextArrival;
                } else {
                    break;
                }
                continue;
            }

            if (prevProcess != processCopies.get(shortestJobIndex).pid) {
                if (prevProcess != -1) {
                    timeline.append("P").append(prevProcess).append("(").append(currentTime).append(") ");
                }
                prevProcess = processCopies.get(shortestJobIndex).pid;
            }


            remainingBurstTime[shortestJobIndex]--;
            currentTime++;

            // Check if the process is completed;
            if (remainingBurstTime[shortestJobIndex] == 0) {
                completedProcesses++;

                // Calculate waiting and turnaround times;
                turnaroundTime[shortestJobIndex] = currentTime - processCopies.get(shortestJobIndex).arrivalTime;
                waitingTime[shortestJobIndex] = turnaroundTime[shortestJobIndex] - processCopies.get(shortestJobIndex).burstTime;
            }
        }

        // Add the final process to the timeline;
        if (prevProcess != -1) {
            timeline.append("P").append(prevProcess).append("(").append(currentTime).append(")");
        }

        System.out.println(timeline.toString());
        System.out.println("\nProcess ID    Arrival Time    Burst Time  Waiting Time    Turnaround Time");

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (int i = 0; i < totalProcesses; i++) {
            Process p = processCopies.get(i);
            totalWaitingTime += waitingTime[i];
            totalTurnaroundTime += turnaroundTime[i];

            System.out.println(p.pid + "\t\t" + p.arrivalTime + "\t\t" +
                    p.burstTime + "\t\t" + waitingTime[i] + "\t\t" + turnaroundTime[i]);
        }

        double avgWaitingTime = (double) totalWaitingTime / totalProcesses;
        double avgTurnaroundTime = (double) totalTurnaroundTime / totalProcesses;
        double cpuUtilization = ((double)(currentTime - idleTime) / currentTime) * 100;
        double throughput = (double) totalProcesses / currentTime;

        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
        System.out.println("CPU Utilization: " + String.format("%.2f%%", cpuUtilization));
        System.out.println("Throughput: " + String.format("%.4f processes/time unit", throughput));
        System.out.println("---------------------------------------------------------\n");
    }

    // Priority Scheduling non-preemptive
    public static void priorityScheduling(List<Process> processes) {
        // Create copies of the processes so we can avoid messing with original list;
        List<Process> processCopies = new ArrayList<>();
        for (Process p : processes) {
            processCopies.add(new Process(p.pid, p.burstTime, p.arrivalTime, p.priority));
        }

        // Sort processes by arrival time first;
        processCopies.sort(Comparator.comparingInt(p -> p.arrivalTime));

        int currentTime = 0;
        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;
        int completedProcesses = 0;
        int idleTime = 0;

        System.out.println("Priority Scheduling ");
        System.out.println("-------------------------------------------------------");
        System.out.println("Process ID\tArrival Time\tBurst Time\tPriority\tWaiting Time\tTurnaround Time");

        StringBuilder timeline = new StringBuilder("Timeline: ");

        while (completedProcesses < processCopies.size()) {
            // Find the highest priority process that has arrived;
            int highestPriorityIndex = -1;
            int highestPriority = Integer.MAX_VALUE;

            for (int i = 0; i < processCopies.size(); i++) {
                Process p = processCopies.get(i);
                if (p.arrivalTime <= currentTime && p.burstTime > 0) {
                    if (p.priority < highestPriority) {
                        highestPriority = p.priority;
                        highestPriorityIndex = i;
                    }
                }
            }

            if (highestPriorityIndex == -1) {
                // if none availible find next;
                int nextArrival = Integer.MAX_VALUE;
                for (Process p : processCopies) {
                    if (p.arrivalTime > currentTime && p.burstTime > 0 && p.arrivalTime < nextArrival) {
                        nextArrival = p.arrivalTime;
                    }
                }

                if (nextArrival != Integer.MAX_VALUE) {
                    timeline.append("IDLE(").append(currentTime).append("-").append(nextArrival).append(") ");
                    idleTime += (nextArrival - currentTime);
                    currentTime = nextArrival;
                } else {
                    break;
                }
                continue;
            }

            Process currentProcess = processCopies.get(highestPriorityIndex);
            int waitingTime = currentTime - currentProcess.arrivalTime;
            int turnaroundTime = waitingTime + currentProcess.burstTime;

            totalWaitingTime += waitingTime;
            totalTurnaroundTime += turnaroundTime;

            System.out.println(currentProcess.pid + "\t\t" +
                    currentProcess.arrivalTime + "\t\t" +
                    currentProcess.burstTime + "\t\t" +
                    currentProcess.priority + "\t\t" +
                    waitingTime + "\t\t" +
                    turnaroundTime);

            timeline.append("P").append(currentProcess.pid).append("(").append(currentTime)
                    .append("-").append(currentTime + currentProcess.burstTime).append(") ");

            currentTime += currentProcess.burstTime;
            currentProcess.burstTime = 0; //done
            completedProcesses++;
        }

        System.out.println("\n" + timeline.toString());

        double avgWaitingTime = (double) totalWaitingTime / processCopies.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processCopies.size();
        double cpuUtilization = ((double)(currentTime - idleTime) / currentTime) * 100;
        double throughput = (double) processCopies.size() / currentTime;

        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
        System.out.println("CPU Utilization: " + String.format("%.2f%%", cpuUtilization));
        System.out.println("Throughput: " + String.format("%.4f processes/time unit", throughput));
        System.out.println("-------------------------------------------------------\n");
    }

    // Multi-Level Feedback Queue Scheduling
    public static void mlfq(List<Process> processes) {

        List<Process> processCopies = new ArrayList<>();
        for (Process p : processes) {
            processCopies.add(new Process(p.pid, p.burstTime, p.arrivalTime, p.priority));
        }


        int numberOfQueues = 3;
        int[] timeQuantum = {2, 4, 8};

        processCopies.sort(Comparator.comparingInt(p -> p.arrivalTime));

        Queue<Process>[] queues = new LinkedList[numberOfQueues];
        for (int i = 0; i < numberOfQueues; i++) {
            queues[i] = new LinkedList<>();
        }

        int currentTime = 0;
        int completedProcesses = 0;
        int idleTime = 0;

        Map<Integer, Integer> startTime = new HashMap<>();
        Map<Integer, Integer> waitingTime = new HashMap<>();
        Map<Integer, Integer> turnaroundTime = new HashMap<>();
        Map<Integer, Integer> remainingBurstTime = new HashMap<>();

        // Initialize with all processes and their remaining burst times;
        for (Process p : processCopies) {
            remainingBurstTime.put(p.pid, p.burstTime);
        }

        System.out.println("Multi-Level Feedback Queue Scheduling");
        System.out.println("-------------------------------------------------------");
        System.out.println("Time quantum: Q0=" + timeQuantum[0] + ", Q1=" + timeQuantum[1] + ", Q2=" + timeQuantum[2]);

        StringBuilder timeline = new StringBuilder("Timeline: ");
        int currentQueue = 0;
        Process runningProcess = null;
        int timeInCurrentQuantum = 0;

        // Add init processes that arrive at time 0;
        for (Process p : processCopies) {
            if (p.arrivalTime == 0) {
                queues[0].add(p);
                startTime.put(p.pid, 0);
            }
        }

        while (completedProcesses < processCopies.size()) {
            for (Process p : processCopies) {
                if (p.arrivalTime == currentTime && !startTime.containsKey(p.pid)) {
                    queues[0].add(p);
                    startTime.put(p.pid, currentTime);
                }
            }

            // If no process running select one from highest priority non-empty queue;
            if (runningProcess == null) {
                for (int i = 0; i < numberOfQueues; i++) {
                    if (!queues[i].isEmpty()) {
                        runningProcess = queues[i].poll();
                        currentQueue = i;
                        timeInCurrentQuantum = 0;
                        break;
                    }
                }

                if (runningProcess == null) {
                    int nextArrival = Integer.MAX_VALUE;
                    for (Process p : processCopies) {
                        if (p.arrivalTime > currentTime && p.arrivalTime < nextArrival &&
                                remainingBurstTime.get(p.pid) > 0) {
                            nextArrival = p.arrivalTime;
                        }
                    }

                    if (nextArrival != Integer.MAX_VALUE) {
                        timeline.append("IDLE(").append(currentTime).append("-").append(nextArrival).append(") ");
                        idleTime += (nextArrival - currentTime);
                        currentTime = nextArrival;
                    } else {
                        break; // Should not reach here if all processes are valid;
                    }
                    continue;
                }
            }

            // Execute the running process for 1 time unit;
            timeline.append("P").append(runningProcess.pid).append("(").append(currentTime).append(") ");
            remainingBurstTime.put(runningProcess.pid, remainingBurstTime.get(runningProcess.pid) - 1);
            timeInCurrentQuantum++;
            currentTime++;

            // Check for new arrivals after incrementing time;
            for (Process p : processCopies) {
                if (p.arrivalTime == currentTime && !startTime.containsKey(p.pid)) {
                    queues[0].add(p);
                    startTime.put(p.pid, currentTime);
                }
            }

            // Check if process is completed;
            if (remainingBurstTime.get(runningProcess.pid) == 0) {
                completedProcesses++;
                turnaroundTime.put(runningProcess.pid, currentTime - runningProcess.arrivalTime);
                waitingTime.put(runningProcess.pid,
                        turnaroundTime.get(runningProcess.pid) - runningProcess.burstTime);
                runningProcess = null;
                continue;
            }

            // Check if time quantum is completed;
            if (timeInCurrentQuantum >= timeQuantum[currentQueue]) {
                if (currentQueue < numberOfQueues - 1) {
                    // Move to next lower priority queue;
                    queues[currentQueue + 1].add(runningProcess);
                } else {
                    // At lowest priority stay in the same queue;
                    queues[currentQueue].add(runningProcess);
                }
                runningProcess = null;
            }
        }

        System.out.println(timeline.toString());
        System.out.println("\nProcess ID\tArrival Time\tBurst Time\tWaiting Time\tTurnaround Time");

        int totalWaitingTime = 0;
        int totalTurnaroundTime = 0;

        for (Process p : processCopies) {
            int wt = waitingTime.get(p.pid);
            int tt = turnaroundTime.get(p.pid);

            totalWaitingTime += wt;
            totalTurnaroundTime += tt;

            System.out.println(p.pid + "\t\t" + p.arrivalTime + "\t\t" +
                    p.burstTime + "\t\t" + wt + "\t\t" + tt);
        }

        double avgWaitingTime = (double) totalWaitingTime / processCopies.size();
        double avgTurnaroundTime = (double) totalTurnaroundTime / processCopies.size();
        double cpuUtilization = ((double)(currentTime - idleTime) / currentTime) * 100;
        double throughput = (double) processCopies.size() / currentTime;

        System.out.println("\nAverage Waiting Time: " + String.format("%.2f", avgWaitingTime));
        System.out.println("Average Turnaround Time: " + String.format("%.2f", avgTurnaroundTime));
        System.out.println("CPU Utilization: " + String.format("%.2f%%", cpuUtilization));
        System.out.println("Throughput: " + String.format("%.4f processes/time unit", throughput));
        System.out.println("-------------------------------------------------------\n");
    }


    public static void main(String[] args) {
        // Generate test processes
        List<Process> identicalProcesses = Process.generateIdenticalProcesses(5);
        List<Process> mixedBurstProcesses = Process.generateMixedBurstProcesses(5);
        List<Process> priorityProcesses = Process.generatePriorityProcesses(5);

        //First edge case all identical arrival times = 0;
        System.out.println("===================================");
        System.out.println("Testing for case where arrival time = 0");
        System.out.println("====================================\n");
        fcfs(identicalProcesses);
        srtf(identicalProcesses);
        priorityScheduling(identicalProcesses);
        mlfq(identicalProcesses);
        //
        System.out.println("====================================");
        System.out.println("Testing for case long and short burst times mixture");
        System.out.println("====================================\n");
        fcfs(mixedBurstProcesses);
        srtf(mixedBurstProcesses);
        priorityScheduling(mixedBurstProcesses);
        mlfq(mixedBurstProcesses);
        System.out.println("====================================");
        System.out.println("Testing for case random priorities");
        System.out.println("====================================\n");
        fcfs(priorityProcesses);
        srtf(priorityProcesses);
        priorityScheduling(priorityProcesses);
        mlfq(priorityProcesses);
    }
}