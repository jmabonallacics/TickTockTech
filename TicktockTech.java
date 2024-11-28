import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.*;
import javax.swing.table.*;




public class TicktockTech {
    public static void main(String[] args) {
        new SchedulingFrame();
    }
}

class SchedulingFrame extends JFrame {
    public SchedulingFrame() {
        setTitle("Scheduling Algorithms");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Dropdown for algorithm selection
        JLabel lblSelect = new JLabel("Select Scheduling Algorithm:");
        lblSelect.setFont(new Font("Arial", Font.BOLD, 16));
        lblSelect.setBounds(20, 20, 250, 30);
        add(lblSelect);

        String[] algorithms = {"Priority Preemptive", "Round Robin", "C-SCAN"};
        JComboBox<String> cbAlgorithms = new JComboBox<>(algorithms);
        cbAlgorithms.setFont(new Font("Arial", Font.PLAIN, 14));
        cbAlgorithms.setBounds(245, 20, 200, 30);
        add(cbAlgorithms);

        // Main panel with CardLayout to switch between algorithm panels
        JPanel mainPanel = new JPanel(new CardLayout());
        mainPanel.setBounds(20, 70, 1300, 550);
        add(mainPanel);

        // Add individual panels for each algorithm
        JPanel priorityPreemptivePanel = createPriorityPreemptivePanel();
        JPanel roundRobinPanel = createRoundRobinPanel();
        JPanel cScanPanel = createCScanPanel();

        mainPanel.add(priorityPreemptivePanel, "Priority Preemptive");
        mainPanel.add(roundRobinPanel, "Round Robin");
        mainPanel.add(cScanPanel, "C-SCAN");

        // Listener to switch panels based on selected algorithm
        cbAlgorithms.addActionListener(e -> {
            CardLayout cl = (CardLayout) (mainPanel.getLayout());
            cl.show(mainPanel, (String) cbAlgorithms.getSelectedItem());
        });

        setLayout(null);
        setVisible(true);
    }

    // Priority Preemptive
    private JPanel createPriorityPreemptivePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
    
        JLabel lblAlgorithm = new JLabel("Priority Preemptive Scheduling");
        lblAlgorithm.setFont(new Font("Arial", Font.BOLD, 16));
        lblAlgorithm.setBounds(20, 10, 400, 30);
        panel.add(lblAlgorithm);
    
        JLabel lblProcesses = new JLabel("Enter the No. of Processes (2-9):");
        lblProcesses.setFont(new Font("Arial", Font.BOLD, 14));
        lblProcesses.setBounds(30, 60, 250, 25);
        panel.add(lblProcesses);
    
        JTextField txtProcesses = new JTextField();
        txtProcesses.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProcesses.setBounds(260,60, 100, 25);
        panel.add(txtProcesses);
    
        JButton btnEnter = new JButton("Enter");
        btnEnter.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEnter.setBounds(365, 60, 80, 25);
        panel.add(btnEnter);
    
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Process", "Arrival Time", "Burst Time", "Priority", "Completion Time", "Turn Around Time", "Waiting Time"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 1 && column <= 3; 
            }
        };
    
        JTable table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 100, 1000, 300);
        panel.add(scrollPane);

        JLabel lblAvgCompletion = new JLabel("Average Completion Time: ");
        lblAvgCompletion.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgCompletion.setBounds(20, 430, 300, 30);
        panel.add(lblAvgCompletion);
    
        JLabel lblAvgTurn = new JLabel("Average Turn Around Time: ");
        lblAvgTurn.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgTurn.setBounds(20, 450, 300, 30);
        panel.add(lblAvgTurn);

        JLabel lblAvgWait = new JLabel("Average Waiting Time: ");
        lblAvgWait.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgWait.setBounds(20, 470, 300, 30);
        panel.add(lblAvgWait);
    
        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setFont(new Font("Arial", Font.BOLD, 14));
        btnCalculate.setBounds(900, 405, 120, 30);
        panel.add(btnCalculate);
    
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        btnClear.setBounds(790, 405, 100, 30);
        panel.add(btnClear);
    
        btnEnter.addActionListener(e -> {
            try {
                int numProcesses = Integer.parseInt(txtProcesses.getText());
                if (numProcesses < 2 || numProcesses > 9) {
                    throw new IllegalArgumentException("Number of processes must be between 2 and 9.");
                }
    
                tableModel.setRowCount(0);
                for (int i = 1; i <= numProcesses; i++) {
                    tableModel.addRow(new Object[]{"P" + i, "", "", "", "", "", ""});
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter a valid number between 2 and 9.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCalculate.addActionListener(e -> {
            int rowCount = tableModel.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(null, "Please add processes first.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            try {
                int[] arrivalTime = new int[rowCount];
                int[] burstTime = new int[rowCount];
                int[] priority = new int[rowCount];
                int[] remainingTime = new int[rowCount];
                int[] completionTime = new int[rowCount];
                int[] turnaroundTime = new int[rowCount];
                int[] waitingTime = new int[rowCount];
        
                double totalWait = 0, totalTurnAround = 0, totalCompletion = 0;
        
                boolean allZeroPriority = true;
        
                // Validate and gather inputs
                for (int i = 0; i < rowCount; i++) {
                    try {
                        arrivalTime[i] = Integer.parseInt((String) tableModel.getValueAt(i, 1));
                        burstTime[i] = Integer.parseInt((String) tableModel.getValueAt(i, 2));
                        priority[i] = Integer.parseInt((String) tableModel.getValueAt(i, 3));
                        remainingTime[i] = burstTime[i];
                        if (priority[i] != 0) {
                            allZeroPriority = false;
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Invalid input in row " + (i + 1) + ". Ensure all fields are filled correctly.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
        
                // Scheduling Logic
                if (allZeroPriority) {
                    // Perform FCFS
                    int currentTime = 0;
        
                    // Sort processes by arrival time
                    Integer[] indices = new Integer[rowCount];
                    for (int i = 0; i < rowCount; i++) indices[i] = i;
                    Arrays.sort(indices, (a, b) -> Integer.compare(arrivalTime[a], arrivalTime[b]));
        
                    for (int i = 0; i < rowCount; i++) {
                        int idx = indices[i];
                        currentTime = Math.max(currentTime, arrivalTime[idx]);
                        currentTime += burstTime[idx];
                        completionTime[idx] = currentTime;
                        turnaroundTime[idx] = completionTime[idx] - arrivalTime[idx];
                        waitingTime[idx] = turnaroundTime[idx] - burstTime[idx];
                    }
                } else {
                    // Perform Preemptive Priority Scheduling
                    int currentTime = 0, completed = 0;
                    boolean[] isCompleted = new boolean[rowCount];
        
                    while (completed < rowCount) {
                        int idx = -1;
                        int highestPriority = Integer.MAX_VALUE;
        
                        // Find the process with the highest priority that is ready to execute
                        for (int i = 0; i < rowCount; i++) {
                            if (!isCompleted[i] && arrivalTime[i] <= currentTime && priority[i] < highestPriority) {
                                highestPriority = priority[i];
                                idx = i;
                            }
                        }
        
                        // If we have found a process to execute
                        if (idx != -1) {
                            // Execute for 1 unit of time (preemptive)
                            remainingTime[idx]--;
        
                            // If the process has finished execution
                            if (remainingTime[idx] == 0) {
                                completionTime[idx] = currentTime + 1;
                                turnaroundTime[idx] = completionTime[idx] - arrivalTime[idx];
                                waitingTime[idx] = turnaroundTime[idx] - burstTime[idx];
                                isCompleted[idx] = true;
                                completed++;
                            }
        
                            currentTime++; 
                        } else {
                   
                            currentTime++;
                        }
                    }
                }
        
                // Update table and calculate averages
                for (int i = 0; i < rowCount; i++) {
                    tableModel.setValueAt(completionTime[i], i, 4);
                    tableModel.setValueAt(turnaroundTime[i], i, 5);
                    tableModel.setValueAt(waitingTime[i], i, 6);
                    totalTurnAround += turnaroundTime[i];
                    totalWait += waitingTime[i];
                    totalCompletion += completionTime[i];
                }
        
                lblAvgCompletion.setText(String.format("Average Completion Time: %.2f", totalCompletion / rowCount));
                lblAvgTurn.setText(String.format("Average Turn Around Time: %.2f", totalTurnAround / rowCount));
                lblAvgWait.setText(String.format("Average Waiting Time: %.2f", totalWait / rowCount));
        
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error in calculation: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
    
        btnClear.addActionListener(e -> {
            txtProcesses.setText("");
            tableModel.setRowCount(0);
            lblAvgCompletion.setText("Average Completion Time: ");
            lblAvgTurn.setText("Average Turn Around Time: ");
            lblAvgWait.setText("Average Waiting Time: ");
        });
    
        return panel;
    }    

    // Round Robin
    private JPanel createRoundRobinPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);

        JLabel lblAlgorithm = new JLabel("Round Robin Scheduling");
        lblAlgorithm.setFont(new Font("Arial", Font.BOLD, 18));
        lblAlgorithm.setBounds(20, 10, 400, 30);
        panel.add(lblAlgorithm);

        JLabel lblProcesses = new JLabel("Enter the No. of Processes (2-9):");
        lblProcesses.setFont(new Font("Arial", Font.BOLD, 14));
        lblProcesses.setBounds(30, 60, 250, 25);
        panel.add(lblProcesses);

        JTextField txtProcesses = new JTextField();
        txtProcesses.setFont(new Font("Arial", Font.PLAIN, 14));
        txtProcesses.setBounds(260,60, 100, 25);
        panel.add(txtProcesses);

        JLabel lblTimeQuantum = new JLabel("Enter Time Quantum: ");
        lblTimeQuantum.setFont(new Font("Arial", Font.BOLD, 14));
        lblTimeQuantum.setBounds(30, 90, 200, 25);
        panel.add(lblTimeQuantum);

        JTextField txtTimeQuantum = new JTextField();
        txtTimeQuantum.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTimeQuantum.setBounds(185, 90, 100, 25);
        panel.add(txtTimeQuantum);
    
        JButton btnEnter = new JButton("Enter");
        btnEnter.setFont(new Font("Arial", Font.PLAIN, 14));
        btnEnter.setBounds(365, 60, 80, 25);
        panel.add(btnEnter);
    
        DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Process", "Arrival Time", "Burst Time", "Completion Time", "Turn Around Time", "Waiting Time"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column >= 1 && column <= 2;
            }
        };
    
        JTable table = new JTable(tableModel);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBounds(20, 120, 1000, 300);
        panel.add(scrollPane);

        JLabel lblAvgCompletion = new JLabel("Average Completion Time: ");
        lblAvgCompletion.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgCompletion.setBounds(20, 450, 300, 30);
        panel.add(lblAvgCompletion);
    
        JLabel lblAvgTurn = new JLabel("Average Turn Around Time: ");
        lblAvgTurn.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgTurn.setBounds(20, 470, 300, 30);
        panel.add(lblAvgTurn);

        JLabel lblAvgWait = new JLabel("Average Waiting Time: ");
        lblAvgWait.setFont(new Font("Arial", Font.BOLD, 14));
        lblAvgWait.setBounds(20, 490, 300, 30);
        panel.add(lblAvgWait);
    
        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setFont(new Font("Arial", Font.BOLD, 14));
        btnCalculate.setBounds(900, 425, 120, 30);
        panel.add(btnCalculate);
    
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        btnClear.setBounds(790, 425, 100, 30);
        panel.add(btnClear);

        btnEnter.addActionListener(e -> {
            try {
                int numProcesses = Integer.parseInt(txtProcesses.getText());
                if (numProcesses < 2 || numProcesses > 9) {
                    throw new IllegalArgumentException("Number of processes must be between 2 and 9.");
                }
        
                // Validate time quantum
                int timeQuantum = Integer.parseInt(txtTimeQuantum.getText());
                if (timeQuantum <= 0) {
                    throw new IllegalArgumentException("Time quantum must be a positive integer.");
                }
        
                tableModel.setRowCount(0);
                for (int i = 1; i <= numProcesses; i++) {
                    tableModel.addRow(new Object[]{"P" + i, "", "", "", "", "", ""});
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numeric values for processes and time quantum.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCalculate.addActionListener(e -> {
            try {
                int timeQuantum = Integer.parseInt(txtTimeQuantum.getText());
                if (timeQuantum <= 0) {
                    JOptionPane.showMessageDialog(null, "Time quantum must be greater than 0", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
        
                int numProcesses = Integer.parseInt(txtProcesses.getText());
                int[] arrivalTime = new int[numProcesses];
                int[] burstTime = new int[numProcesses];
                int[] completionTime = new int[numProcesses];
                int[] turnAroundTime = new int[numProcesses];
                int[] waitTime = new int[numProcesses];
        
                // Fill in the arrival and burst times from the table
                for (int i = 0; i < numProcesses; i++) {
                    try {
                        arrivalTime[i] = Integer.parseInt((String) tableModel.getValueAt(i, 1));
                        burstTime[i] = Integer.parseInt((String) tableModel.getValueAt(i, 2));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Please enter valid numbers for Arrival and Burst Times", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
        
                // Perform Round Robin Scheduling
                roundRobinScheduling(numProcesses, arrivalTime, burstTime, waitTime, turnAroundTime, completionTime, timeQuantum);
        
                // Calculate and update the table
                float totalWaitTime = 0, totalTurnAroundTime = 0, totalCompletionTime = 0;
                for (int i = 0; i < numProcesses; i++) {
                    tableModel.setValueAt(completionTime[i], i, 3);
                    tableModel.setValueAt(turnAroundTime[i], i, 4);
                    tableModel.setValueAt(waitTime[i], i, 5);
        
                    totalCompletionTime += completionTime[i];
                    totalTurnAroundTime += turnAroundTime[i];
                    totalWaitTime += waitTime[i];
                }
        
                // Calculate averages and display
                lblAvgCompletion.setText("Average Completion Time: " + String.format("%.2f", totalCompletionTime / numProcesses));
                lblAvgTurn.setText("Average Turn Around Time: " + String.format("%.2f", totalTurnAroundTime / numProcesses));
                lblAvgWait.setText("Average Waiting Time: " + String.format("%.2f", totalWaitTime / numProcesses));
        
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Please enter valid numeric values for processes and time quantum.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
    
        btnClear.addActionListener(e -> {
            txtProcesses.setText("");
            txtTimeQuantum.setText("");
            tableModel.setRowCount(0);
            lblAvgCompletion.setText("Average Completion Time: ");
            lblAvgTurn.setText("Average Turn Around Time: ");
            lblAvgWait.setText("Average Waiting Time: ");
        });
    
        return panel;
    }

    private void roundRobinScheduling(int numProcesses, int[] arrivalTime, int[] burstTime, int[] waitTime, int[] turnAroundTime, int[] completionTime, int timeQuantum) {
        int[] remainingBurstTime = new int[numProcesses]; 
        Queue<Integer> queue = new LinkedList<>();
        
        // Initialize remaining burst time and enqueue all processes
        for (int i = 0; i < numProcesses; i++) {
            remainingBurstTime[i] = burstTime[i];
            queue.offer(i);
        }
        
        int currentTime = 0; 
        boolean[] isCompleted = new boolean[numProcesses]; 
    
        while (!queue.isEmpty()) {
            int processIndex = queue.poll();
    
            // If the process is not yet completed
            if (remainingBurstTime[processIndex] > 0) {
                int executionTime = Math.min(remainingBurstTime[processIndex], timeQuantum); 
                remainingBurstTime[processIndex] -= executionTime;
                currentTime += executionTime;
    
                // If the process is completed
                if (remainingBurstTime[processIndex] == 0) {
                    isCompleted[processIndex] = true;
                    completionTime[processIndex] = currentTime; 
                }
    
                // Enqueue the process again if not completed
                if (remainingBurstTime[processIndex] > 0) {
                    queue.offer(processIndex);
                }
            }
        }
    
        // Calculate Turnaround and Waiting Time for each process
        for (int i = 0; i < numProcesses; i++) {
            turnAroundTime[i] = completionTime[i] - arrivalTime[i];
            waitTime[i] = turnAroundTime[i] - burstTime[i]; 
        }
    }  

    // C-Scan
    private JPanel createCScanPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
    
        JLabel lblAlgorithm = new JLabel("C-SCAN Disk Scheduling");
        lblAlgorithm.setFont(new Font("Arial", Font.BOLD, 16));
        lblAlgorithm.setBounds(20, 10, 400, 30);
        panel.add(lblAlgorithm);
    
        JLabel lblHead = new JLabel("Enter Current Position:");
        lblHead.setFont(new Font("Arial", Font.BOLD, 14));
        lblHead.setBounds(30, 60, 250, 25);
        panel.add(lblHead);
    
        JTextField txtHead = new JTextField();
        txtHead.setFont(new Font("Arial", Font.PLAIN, 14));
        txtHead.setBounds(195, 60, 100, 25);
        panel.add(txtHead);
    
        JLabel lblTrackSize = new JLabel("Enter Track Size:");
        lblTrackSize.setFont(new Font("Arial", Font.BOLD, 14));
        lblTrackSize.setBounds(30, 100, 250, 25);
        panel.add(lblTrackSize);
    
        JTextField txtTrackSize = new JTextField();
        txtTrackSize.setFont(new Font("Arial", Font.PLAIN, 14));
        txtTrackSize.setBounds(155, 100, 100, 25);
        panel.add(txtTrackSize);
    
        JLabel lblSeekRate = new JLabel("Enter Seek Rate:");
        lblSeekRate.setFont(new Font("Arial", Font.BOLD, 14));
        lblSeekRate.setBounds(30, 140, 250, 25);
        panel.add(lblSeekRate);
    
        JTextField txtSeekRate = new JTextField();
        txtSeekRate.setFont(new Font("Arial", Font.PLAIN, 14));
        txtSeekRate.setBounds(155, 140, 100, 25);
        panel.add(txtSeekRate);
    
        JLabel lblNumRequests = new JLabel("Enter Number of Requests (1-10):");
        lblNumRequests.setFont(new Font("Arial", Font.BOLD, 14));
        lblNumRequests.setBounds(30, 180, 300, 25);
        panel.add(lblNumRequests);
    
        JTextField txtNumRequests = new JTextField();
        txtNumRequests.setFont(new Font("Arial", Font.PLAIN, 14));
        txtNumRequests.setBounds(270, 180, 50, 25);
        panel.add(txtNumRequests);
    
        JPanel requestPanel = new JPanel();
        requestPanel.setLayout(new GridLayout(0, 2));
        JScrollPane scrollPane = new JScrollPane(requestPanel);
        scrollPane.setBounds(450, 100, 350, 280);
        panel.add(scrollPane);
    
        JLabel lblTotalMovement = new JLabel("Total Head Movement: ");
        lblTotalMovement.setFont(new Font("Arial", Font.BOLD, 16));
        lblTotalMovement.setBounds(30, 380, 400, 25);
        panel.add(lblTotalMovement);
    
        JLabel lblSeekTime = new JLabel("Seek Time: ");
        lblSeekTime.setFont(new Font("Arial", Font.BOLD, 16));
        lblSeekTime.setBounds(30, 410, 400, 25);
        panel.add(lblSeekTime);

        JLabel lblSeekSequence = new JLabel("Seek Sequence: ");
        lblSeekSequence.setFont(new Font("Arial", Font.BOLD, 16));
        lblSeekSequence.setBounds(30, 435, 450, 40);
        panel.add(lblSeekSequence);

    
        JButton btnGenerateFields = new JButton("Generate Fields");
        btnGenerateFields.setFont(new Font("Arial", Font.BOLD, 14));
        btnGenerateFields.setBounds(30, 210, 170, 30);
        panel.add(btnGenerateFields);
    
        JButton btnCalculate = new JButton("Calculate");
        btnCalculate.setFont(new Font("Arial", Font.BOLD, 14));
        btnCalculate.setBounds(700, 385, 100, 30);
        panel.add(btnCalculate);
    
        JButton btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Arial", Font.BOLD, 14));
        btnClear.setBounds(590, 385, 100, 30);
        panel.add(btnClear);
    

        // Generate Input Fields for Requests
        btnGenerateFields.addActionListener(e -> {
            requestPanel.removeAll();
            try {
                int numRequests = Integer.parseInt(txtNumRequests.getText().trim());
                if (numRequests < 1 || numRequests > 10) {
                    JOptionPane.showMessageDialog(panel, "Number of requests must be between 1 and 10.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                for (int i = 0; i < numRequests; i++) {
                    
                    JLabel locationLabel = new JLabel("Location " + (i + 1) + ":");
                    locationLabel.setFont(new Font("Arial", Font.BOLD, 14));
                    locationLabel.setHorizontalAlignment(JLabel.CENTER);  
                    
                    
                    JTextField field = new JTextField();
                    field.setFont(new Font("Arial", Font.PLAIN, 14));  
                    field.setHorizontalAlignment(JTextField.CENTER);

                    
                    
                    requestPanel.add(locationLabel);
                    requestPanel.add(field);
                    
                    requestPanel.revalidate();
                    requestPanel.repaint();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid number of requests. Enter a valid integer.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
    
        // Calculate C-SCAN
        btnCalculate.addActionListener(e -> {
            try {
                int currentPosition = Integer.parseInt(txtHead.getText().trim());
                int trackSize = Integer.parseInt(txtTrackSize.getText().trim());
                int seekRate = Integer.parseInt(txtSeekRate.getText().trim());

                int numRequests = Integer.parseInt(txtNumRequests.getText().trim());
                int[] requests = new int[numRequests];
                for (int i = 0; i < numRequests; i++) {
                    JTextField field = (JTextField) requestPanel.getComponent(i * 2 + 1);
                    requests[i] = Integer.parseInt(field.getText().trim());
                }

                // Sort the requests in ascending order
                Arrays.sort(requests);
                List<Integer> seekSequence = new ArrayList<>();
                int totalMovement = 0;

                // Start moving towards the larger number (right direction)
                int index = Arrays.binarySearch(requests, currentPosition);
                if (index < 0) index = -(index + 1);

                // Move in the right direction (towards larger numbers)
                for (int i = index; i < numRequests; i++) {
                    seekSequence.add(requests[i]);
                    totalMovement += Math.abs(requests[i] - currentPosition);
                    currentPosition = requests[i];
                }

                // Move to the end of the disk (trackSize - 1)
                seekSequence.add(trackSize - 1);
                totalMovement += Math.abs(trackSize - 1 - currentPosition);
                currentPosition = trackSize - 1;

                // Wrap around to the start of the disk (0)
                seekSequence.add(0);
                totalMovement += Math.abs(currentPosition - 0);
                currentPosition = 0;

                // Process remaining requests on the left
                for (int i = 0; i < index; i++) {
                    seekSequence.add(requests[i]);
                    totalMovement += Math.abs(requests[i] - currentPosition);
                    currentPosition = requests[i];
                }

                // Calculate the seek time
                int seekTime = totalMovement * seekRate;

                // Update labels
                lblTotalMovement.setText("Total Head Movement: " + totalMovement);
                lblSeekTime.setText("Seek Time: " + seekTime + " ms");
                lblSeekSequence.setText("Seek Sequence: " + seekSequence);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(panel, "Invalid input. Enter numeric values only.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Clear Fields
        btnClear.addActionListener(e -> {
            txtHead.setText("");
            txtTrackSize.setText("");
            txtSeekRate.setText("");
            txtNumRequests.setText("");
            requestPanel.removeAll();
            requestPanel.revalidate();
            requestPanel.repaint();
            lblTotalMovement.setText("Total Head Movement: ");
            lblSeekTime.setText("Seek Time: ");
            lblSeekSequence.setText("Seek Sequence: ");
        });        
    
        return panel;
    }    
}    
