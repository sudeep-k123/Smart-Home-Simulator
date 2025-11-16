// SmartHomeGUI.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SmartHomeGUI extends JFrame implements Observer {
    private SmartHomeController controller;
    private JTabbedPane tabbedPane;
    private JTable deviceTable;
    private JTable taskTable;
    private DefaultTableModel deviceTableModel;
    private DefaultTableModel taskTableModel;
    private JTextArea logArea;
    private JPanel dashboardPanel; // Keep reference to dashboard panel

    // Color scheme
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // Steel Blue
    private final Color SECONDARY_COLOR = new Color(240, 248, 255); // Alice Blue
    private final Color ACCENT_COLOR = new Color(255, 140, 0); // Dark Orange
    private final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private final Color SUCCESS_COLOR = new Color(46, 139, 87); // Sea Green
    private final Color WARNING_COLOR = new Color(255, 99, 71); // Tomato

    public SmartHomeGUI() {
        controller = SmartHomeController.getInstance();
        controller.addSystemObserver(this);
        initializeUI();
        loadData();
        applyStyles();
    }

    private void initializeUI() {
        setTitle("🏠 Smart Home Automation Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // Create tabbed pane with modern look
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Create tabs
        tabbedPane.addTab("📊 Dashboard", createDashboardPanel());
        tabbedPane.addTab("🔌 Devices", createDevicesPanel());
        tabbedPane.addTab("⏰ Schedule", createSchedulePanel());
        tabbedPane.addTab("➕ Add Device", createAddDevicePanel());
        tabbedPane.addTab("📝 System Log", createLogPanel());

        add(tabbedPane);
    }

    private void applyStyles() {
        // Set look and feel
        try {
            // FIXED LINE: Changed getSystemLookAndFeel() to getSystemLookAndFeelClassName()
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Style the frame
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Style tabs
        tabbedPane.setBackground(SECONDARY_COLOR);
        tabbedPane.setForeground(PRIMARY_COLOR);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        titlePanel.setBackground(BACKGROUND_COLOR);
        JLabel titleLabel = new JLabel("🏠 Smart Home Dashboard");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Device status panel
        JPanel statusPanel = new JPanel(new GridLayout(0, 2, 15, 15));
        statusPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Device Status",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        statusPanel.setBackground(BACKGROUND_COLOR);

        dashboardPanel = statusPanel; // Store reference
        refreshDashboardStatus(statusPanel);

        JScrollPane scrollPane = new JScrollPane(statusPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        JButton refreshButton = new JButton("🔄 Refresh Status");
        styleButton(refreshButton, PRIMARY_COLOR);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshDashboardStatus(dashboardPanel);
            }
        });
        buttonPanel.add(refreshButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshDashboardStatus(JPanel statusPanel) {
        statusPanel.removeAll();
        List<Device> devices = controller.getDevices();
        if (devices.isEmpty()) {
            JLabel emptyLabel = new JLabel("No devices found. Add devices using the 'Add Device' tab.");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            emptyLabel.setForeground(Color.GRAY);
            statusPanel.add(emptyLabel);
        } else {
            for (Device device : devices) {
                JPanel devicePanel = createDeviceCard(device);
                statusPanel.add(devicePanel);
            }
        }
        statusPanel.revalidate();
        statusPanel.repaint();
    }

    private JPanel createDeviceCard(Device device) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(device.isOn() ? SUCCESS_COLOR : Color.LIGHT_GRAY, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(300, 180));

        // Device header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(device.isOn() ? new Color(240, 255, 240) : new Color(245, 245, 245));
        JLabel nameLabel = new JLabel(device.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        nameLabel.setForeground(PRIMARY_COLOR);
        JLabel typeLabel = new JLabel(device.getClass().getSimpleName());
        typeLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        typeLabel.setForeground(Color.GRAY);

        JPanel namePanel = new JPanel(new GridLayout(2, 1));
        namePanel.setBackground(headerPanel.getBackground());
        namePanel.add(nameLabel);
        namePanel.add(typeLabel);
        headerPanel.add(namePanel, BorderLayout.WEST);

        // Status indicator
        JLabel statusLabel = new JLabel(device.isOn() ? "ON" : "OFF");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusLabel.setForeground(device.isOn() ? SUCCESS_COLOR : WARNING_COLOR);
        headerPanel.add(statusLabel, BorderLayout.EAST);
        card.add(headerPanel, BorderLayout.NORTH);

        // Device status details
        JTextArea statusArea = new JTextArea();
        statusArea.setText(device.getStatus());
        statusArea.setEditable(false);
        statusArea.setLineWrap(true);
        statusArea.setWrapStyleWord(true);
        statusArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusArea.setBackground(card.getBackground());
        // FIXED LINE: Added foreground color to ensure text is visible
        statusArea.setForeground(Color.BLACK);
        statusArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        card.add(new JScrollPane(statusArea), BorderLayout.CENTER);

        // Control buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        buttonPanel.setBackground(card.getBackground());

        JButton toggleButton = new JButton(device.isOn() ? "🔌 Turn Off" : "💡 Turn On");
        styleButton(toggleButton, device.isOn() ? WARNING_COLOR : SUCCESS_COLOR);
        toggleButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        toggleButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                device.toggle();
                refreshDashboardStatus(dashboardPanel);
                refreshDeviceTable();
            }
        });
        buttonPanel.add(toggleButton);

        // Device-specific controls
        if (device instanceof Light) {
            JButton brightnessButton = new JButton("🌈 Adjust");
            styleButton(brightnessButton, ACCENT_COLOR);
            brightnessButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            brightnessButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showLightDialog((Light) device);
                }
            });
            buttonPanel.add(brightnessButton);
        } else if (device instanceof Fan) {
            JButton speedButton = new JButton("🌀 Adjust");
            styleButton(speedButton, ACCENT_COLOR);
            speedButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            speedButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showFanDialog((Fan) device);
                }
            });
            buttonPanel.add(speedButton);
        } else if (device instanceof AirConditioner) {
            JButton tempButton = new JButton("❄️ Adjust");
            styleButton(tempButton, ACCENT_COLOR);
            tempButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tempButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAcDialog((AirConditioner) device);
                }
            });
            buttonPanel.add(tempButton);
        } else if (device instanceof Thermostat) {
            JButton tempButton = new JButton("🌡️ Adjust");
            styleButton(tempButton, ACCENT_COLOR);
            tempButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            tempButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showThermostatDialog((Thermostat) device);
                }
            });
            buttonPanel.add(tempButton);
        } else if (device instanceof DoorLock) {
            JButton lockButton = new JButton(((DoorLock) device).isLocked() ? "🔓 Unlock" : "🔒 Lock");
            styleButton(lockButton, ((DoorLock) device).isLocked() ? SUCCESS_COLOR : WARNING_COLOR);
            lockButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lockButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    device.toggle();
                    refreshDashboardStatus(dashboardPanel);
                    refreshDeviceTable();
                }
            });
            buttonPanel.add(lockButton);
        } else if (device instanceof SecurityCamera) {
            JButton recordButton = new JButton(((SecurityCamera) device).isRecording() ? "⏹️ Stop" : "⏺️ Record");
            styleButton(recordButton, ((SecurityCamera) device).isRecording() ? WARNING_COLOR : SUCCESS_COLOR);
            recordButton.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            recordButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ((SecurityCamera) device).toggleRecording();
                    refreshDashboardStatus(dashboardPanel);
                    refreshDeviceTable();
                }
            });
            buttonPanel.add(recordButton);
        }

        card.add(buttonPanel, BorderLayout.SOUTH);
        return card;
    }

    private JPanel createDevicesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("🔌 Manage Devices");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Device Name", "Type", "Status", "Actions"};
        deviceTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only Actions column is editable
            }
        };

        // Create table
        deviceTable = new JTable(deviceTableModel);
        deviceTable.setFillsViewportHeight(true);
        deviceTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        deviceTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        deviceTable.getTableHeader().setBackground(PRIMARY_COLOR);
        deviceTable.getTableHeader().setForeground(Color.WHITE);
        deviceTable.setRowHeight(25);
        deviceTable.setSelectionBackground(new Color(173, 216, 230));

        // Add buttons to Actions column
        deviceTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        deviceTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(deviceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = new JButton("🔄 Refresh");
        styleButton(refreshButton, PRIMARY_COLOR);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshDeviceTable();
            }
        });
        buttonPanel.add(refreshButton);

        JButton removeButton = new JButton("🗑️ Remove Selected");
        styleButton(removeButton, WARNING_COLOR);
        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedDevice();
            }
        });
        buttonPanel.add(removeButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshDeviceTable();
        return panel;
    }

    private void refreshDeviceTable() {
        deviceTableModel.setRowCount(0); // Clear existing rows
        List<Device> devices = controller.getDevices();
        for (Device device : devices) {
            Object[] row = {
                device.getName(),
                device.getClass().getSimpleName(),
                device.getStatus(),
                "Manage"
            };
            deviceTableModel.addRow(row);
        }
    }

    private void removeSelectedDevice() {
        int selectedRow = deviceTable.getSelectedRow();
        if (selectedRow >= 0) {
            String deviceName = (String) deviceTableModel.getValueAt(selectedRow, 0);
            // Find the device by name
            Device deviceToRemove = null;
            for (Device device : controller.getDevices()) {
                if (device.getName().equals(deviceName)) {
                    deviceToRemove = device;
                    break;
                }
            }
            if (deviceToRemove != null) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to remove " + deviceName + "?",
                    "Confirm Removal",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.removeDevice(deviceToRemove.getId());
                    refreshDeviceTable();
                    refreshDashboardStatus(dashboardPanel);
                    appendToLog("Device removed: " + deviceName);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a device to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    private JPanel createSchedulePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("⏰ Schedule Tasks");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create table model
        String[] columnNames = {"Task", "Time", "Recurring", "Enabled", "Actions"};
        taskTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4; // Enabled and Actions columns
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 3) return Boolean.class; // Enabled column
                return super.getColumnClass(columnIndex);
            }
        };

        // Create table
        taskTable = new JTable(taskTableModel);
        taskTable.setFillsViewportHeight(true);
        taskTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        taskTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        taskTable.getTableHeader().setBackground(PRIMARY_COLOR);
        taskTable.getTableHeader().setForeground(Color.WHITE);
        taskTable.setRowHeight(25);
        taskTable.setSelectionBackground(new Color(173, 216, 230));

        // Add checkboxes to Enabled column
        taskTable.getColumnModel().getColumn(3).setCellRenderer(new CheckBoxRenderer());
        taskTable.getColumnModel().getColumn(3).setCellEditor(new CheckBoxEditor());

        // Add buttons to Actions column
        taskTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        taskTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(taskTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Add task form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Add New Scheduled Task",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        formPanel.setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Device selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel deviceLabel = new JLabel("Device:");
        deviceLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(deviceLabel, gbc);
        gbc.gridx = 1;
        JComboBox<String> deviceCombo = new JComboBox<>();
        deviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        refreshDeviceComboBox(deviceCombo);
        formPanel.add(deviceCombo, gbc);

        // Action selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel actionLabel = new JLabel("Action:");
        actionLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(actionLabel, gbc);
        gbc.gridx = 1;
        String[] actions = {"turn on", "turn off", "toggle", "set brightness 75", "set temperature 22", "set speed 3"};
        JComboBox<String> actionCombo = new JComboBox<>(actions);
        actionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(actionCombo, gbc);

        // Time selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel timeLabel = new JLabel("Time (HH:mm):");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(timeLabel, gbc);
        gbc.gridx = 1;
        JTextField timeField = new JTextField(15);
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm")));
        formPanel.add(timeField, gbc);

        // Recurring checkbox
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel recurringLabel = new JLabel("Recurring:");
        recurringLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(recurringLabel, gbc);
        gbc.gridx = 1;
        JCheckBox recurringCheck = new JCheckBox("Daily");
        recurringCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        recurringCheck.setSelected(true);
        formPanel.add(recurringCheck, gbc);

        // Add button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton addButton = new JButton("➕ Add Task");
        styleButton(addButton, SUCCESS_COLOR);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String deviceName = (String) deviceCombo.getSelectedItem();
                String action = (String) actionCombo.getSelectedItem();
                String time = timeField.getText();
                boolean recurring = recurringCheck.isSelected();

                if (deviceName != null && !deviceName.isEmpty()) {
                    ScheduledTask task = new ScheduledTask(deviceName, action, time, recurring);
                    controller.addScheduledTask(task);
                    refreshTaskTable();
                    timeField.setText(LocalTime.now().format(DateTimeFormatter.ofPattern("H:mm")));
                    appendToLog("Scheduled task added: " + task.toString());
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Task added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Please select a device.", "Warning", JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        formPanel.add(addButton, gbc);

        panel.add(formPanel, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton refreshButton = new JButton("🔄 Refresh");
        styleButton(refreshButton, PRIMARY_COLOR);
        refreshButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                refreshTaskTable();
            }
        });
        buttonPanel.add(refreshButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Initial load
        refreshTaskTable();
        return panel;
    }

    private void refreshDeviceComboBox(JComboBox<String> comboBox) {
        comboBox.removeAllItems();
        List<Device> devices = controller.getDevices();
        for (Device device : devices) {
            comboBox.addItem(device.getName());
        }
    }

    private void refreshTaskTable() {
        taskTableModel.setRowCount(0); // Clear existing rows
        List<ScheduledTask> tasks = controller.getScheduledTasks();
        for (ScheduledTask task : tasks) {
            Object[] row = {
                task.getDeviceName() + " - " + task.getAction(),
                task.getTimeAsString(),
                task.isRecurring() ? "Yes" : "No",
                task.isEnabled(),
                "Manage"
            };
            taskTableModel.addRow(row);
        }
    }

    private void toggleTaskEnabled(int row) {
        ScheduledTask taskToToggle = null;
        int rowIndex = 0;
        for (ScheduledTask task : controller.getScheduledTasks()) {
            if (rowIndex == row) {
                taskToToggle = task;
                break;
            }
            rowIndex++;
        }
        if (taskToToggle != null) {
            taskToToggle.setEnabled(!taskToToggle.isEnabled());
            refreshTaskTable();
            appendToLog("Task " + (taskToToggle.isEnabled() ? "enabled" : "disabled") + ": " + taskToToggle.toString());
        }
    }

    private void manageTask(int row) {
        ScheduledTask taskToManage = null;
        int rowIndex = 0;
        for (ScheduledTask task : controller.getScheduledTasks()) {
            if (rowIndex == row) {
                taskToManage = task;
                break;
            }
            rowIndex++;
        }
        if (taskToManage != null) {
            JDialog dialog = new JDialog(this, "Manage Scheduled Task", true);
            dialog.setLayout(new GridBagLayout());
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(this);
            dialog.getContentPane().setBackground(SECONDARY_COLOR);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(8, 8, 8, 8);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            // Device selection
            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel deviceLabel = new JLabel("Device:");
            deviceLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dialog.add(deviceLabel, gbc);
            gbc.gridx = 1;
            JComboBox<String> deviceCombo = new JComboBox<>();
            deviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            refreshDeviceComboBox(deviceCombo);
            deviceCombo.setSelectedItem(taskToManage.getDeviceName());
            dialog.add(deviceCombo, gbc);

            // Action selection
            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel actionLabel = new JLabel("Action:");
            actionLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dialog.add(actionLabel, gbc);
            gbc.gridx = 1;
            String[] actions = {"turn on", "turn off", "toggle", "set brightness 75", "set temperature 22", "set speed 3"};
            JComboBox<String> actionCombo = new JComboBox<>(actions);
            actionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            actionCombo.setSelectedItem(taskToManage.getAction());
            dialog.add(actionCombo, gbc);

            // Time selection
            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel timeLabel = new JLabel("Time (HH:mm):");
            timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dialog.add(timeLabel, gbc);
            gbc.gridx = 1;
            JTextField timeField = new JTextField(15);
            timeField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            timeField.setText(taskToManage.getTimeAsString());
            dialog.add(timeField, gbc);

            // Recurring checkbox
            gbc.gridx = 0;
            gbc.gridy = 3;
            JLabel recurringLabel = new JLabel("Recurring:");
            recurringLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dialog.add(recurringLabel, gbc);
            gbc.gridx = 1;
            JCheckBox recurringCheck = new JCheckBox("Daily");
            recurringCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            recurringCheck.setSelected(taskToManage.isRecurring());
            dialog.add(recurringCheck, gbc);

            // Enabled checkbox
            gbc.gridx = 0;
            gbc.gridy = 4;
            JLabel enabledLabel = new JLabel("Enabled:");
            enabledLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dialog.add(enabledLabel, gbc);
            gbc.gridx = 1;
            JCheckBox enabledCheck = new JCheckBox();
            enabledCheck.setSelected(taskToManage.isEnabled());
            dialog.add(enabledCheck, gbc);

            // Buttons
            gbc.gridx = 0;
            gbc.gridy = 5;
            gbc.gridwidth = 2;
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            buttonPanel.setBackground(dialog.getContentPane().getBackground());

            JButton updateButton = new JButton("💾 Update");
            styleButton(updateButton, SUCCESS_COLOR);
            updateButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    taskToManage.setDeviceName((String) deviceCombo.getSelectedItem());
                    taskToManage.setAction((String) actionCombo.getSelectedItem());
                    try {
                        String[] timeParts = timeField.getText().split(":");
                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);
                        taskToManage.setExecutionTime(LocalTime.of(hour, minute));
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Invalid time format. Use HH:mm", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    taskToManage.setRecurring(recurringCheck.isSelected());
                    taskToManage.setEnabled(enabledCheck.isSelected());
                    refreshTaskTable();
                    dialog.dispose();
                    appendToLog("Task updated: " + taskToManage.toString());
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Task updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            });
            buttonPanel.add(updateButton);

            JButton deleteButton = new JButton("🗑️ Delete");
            styleButton(deleteButton, WARNING_COLOR);
            deleteButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int confirm = JOptionPane.showConfirmDialog(
                        dialog,
                        "Are you sure you want to delete this task?",
                        "Confirm Deletion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE
                    );
                    if (confirm == JOptionPane.YES_OPTION) {
                        controller.removeScheduledTask(taskToManage.getId());
                        refreshTaskTable();
                        dialog.dispose();
                        appendToLog("Task deleted: " + taskToManage.toString());
                        JOptionPane.showMessageDialog(SmartHomeGUI.this, "Task deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            });
            buttonPanel.add(deleteButton);

            JButton cancelButton = new JButton("❌ Cancel");
            styleButton(cancelButton, Color.GRAY);
            cancelButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            buttonPanel.add(cancelButton);

            dialog.add(buttonPanel, gbc);
            dialog.setVisible(true);
        }
    }

    private JPanel createAddDevicePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("➕ Add New Device");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
            "Device Configuration",
            TitledBorder.CENTER,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 16),
            PRIMARY_COLOR
        ));
        formPanel.setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Device type selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel typeLabel = new JLabel("Device Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(typeLabel, gbc);
        gbc.gridx = 1;
        String[] deviceTypes = {"Light", "Fan", "AirConditioner", "Thermostat", "DoorLock", "SecurityCamera"};
        JComboBox<String> typeCombo = new JComboBox<>(deviceTypes);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(typeCombo, gbc);

        // Device name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Device Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JTextField nameField = new JTextField(20);
        nameField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        formPanel.add(nameField, gbc);

        // Additional fields panel (changes based on device type)
        JPanel additionalFieldsPanel = new JPanel(new GridBagLayout());
        additionalFieldsPanel.setBackground(formPanel.getBackground());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        formPanel.add(additionalFieldsPanel, gbc);

        // Update additional fields when device type changes
        typeCombo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateAdditionalFields(additionalFieldsPanel, (String) typeCombo.getSelectedItem());
            }
        });
        updateAdditionalFields(additionalFieldsPanel, (String) typeCombo.getSelectedItem());

        // Add button
        gbc.gridy = 3;
        JButton addButton = new JButton("➕ Add Device");
        styleButton(addButton, SUCCESS_COLOR);
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String typeName = (String) typeCombo.getSelectedItem();
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Please enter a device name.", "Warning", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                Device newDevice = createDevice(typeName, name, additionalFieldsPanel);
                if (newDevice != null) {
                    controller.addDevice(newDevice);
                    refreshDeviceTable();
                    refreshDashboardStatus(dashboardPanel);
                    nameField.setText("");
                    appendToLog("Device added: " + newDevice.getName());
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Device added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        formPanel.add(addButton, gbc);

        panel.add(formPanel, BorderLayout.CENTER);
        return panel;
    }

    private void updateAdditionalFields(JPanel panel, String deviceType) {
        panel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        switch (deviceType) {
            case "Light":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel brightnessLabel = new JLabel("Initial Brightness (0-100):");
                brightnessLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(brightnessLabel, gbc);
                gbc.gridx = 1;
                JSpinner brightnessSpinner = new JSpinner(new SpinnerNumberModel(50, 0, 100, 1));
                brightnessSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(brightnessSpinner, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel colorLabel = new JLabel("Color:");
                colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(colorLabel, gbc);
                gbc.gridx = 1;
                String[] colors = {"warm", "cool", "daylight"};
                JComboBox<String> colorCombo = new JComboBox<>(colors);
                colorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(colorCombo, gbc);
                break;

            case "Fan":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel speedLabel = new JLabel("Initial Speed (1-5):");
                speedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(speedLabel, gbc);
                gbc.gridx = 1;
                JSpinner speedSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
                speedSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(speedSpinner, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel oscillatingLabel = new JLabel("Oscillating:");
                oscillatingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(oscillatingLabel, gbc);
                gbc.gridx = 1;
                JCheckBox oscillatingCheck = new JCheckBox();
                panel.add(oscillatingCheck, gbc);
                break;

            case "AirConditioner":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel tempLabel = new JLabel("Initial Temperature (16-30°C):");
                tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(tempLabel, gbc);
                gbc.gridx = 1;
                JSpinner tempSpinner = new JSpinner(new SpinnerNumberModel(22, 16, 30, 1));
                tempSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(tempSpinner, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel modeLabel = new JLabel("Mode:");
                modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(modeLabel, gbc);
                gbc.gridx = 1;
                String[] acModes = {"cool", "heat", "fan", "dry"};
                JComboBox<String> acModeCombo = new JComboBox<>(acModes);
                acModeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(acModeCombo, gbc);
                break;

            case "Thermostat":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel targetLabel = new JLabel("Target Temperature (10-35°C):");
                targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(targetLabel, gbc);
                gbc.gridx = 1;
                JSpinner targetSpinner = new JSpinner(new SpinnerNumberModel(22, 10, 35, 1));
                targetSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(targetSpinner, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel currentLabel = new JLabel("Current Temperature (-10-50°C):");
                currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(currentLabel, gbc);
                gbc.gridx = 1;
                JSpinner currentSpinner = new JSpinner(new SpinnerNumberModel(22, -10, 50, 1));
                currentSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(currentSpinner, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel thermoModeLabel = new JLabel("Mode:");
                thermoModeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(thermoModeLabel, gbc);
                gbc.gridx = 1;
                String[] thermoModes = {"heat", "cool", "off"};
                JComboBox<String> thermoModeCombo = new JComboBox<>(thermoModes);
                thermoModeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                panel.add(thermoModeCombo, gbc);
                break;

            case "DoorLock":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel locationLabel = new JLabel("Location:");
                locationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(locationLabel, gbc);
                gbc.gridx = 1;
                JTextField locationField = new JTextField(15);
                locationField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                locationField.setText("Front Door");
                panel.add(locationField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel lockedLabel = new JLabel("Initially Locked:");
                lockedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(lockedLabel, gbc);
                gbc.gridx = 1;
                JCheckBox lockedCheck = new JCheckBox();
                lockedCheck.setSelected(true);
                panel.add(lockedCheck, gbc);
                break;

            case "SecurityCamera":
                gbc.gridx = 0;
                gbc.gridy = 0;
                JLabel camLocationLabel = new JLabel("Location:");
                camLocationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(camLocationLabel, gbc);
                gbc.gridx = 1;
                JTextField camLocationField = new JTextField(15);
                camLocationField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                camLocationField.setText("Living Room");
                panel.add(camLocationField, gbc);

                gbc.gridx = 0;
                gbc.gridy = 1;
                JLabel resLabel = new JLabel("Resolution:");
                resLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(resLabel, gbc);
                gbc.gridx = 1;
                String[] resolutions = {"720", "1080", "4K"};
                JComboBox<String> resCombo = new JComboBox<>(resolutions);
                resCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                resCombo.setSelectedIndex(1); // Default to 1080p
                panel.add(resCombo, gbc);

                gbc.gridx = 0;
                gbc.gridy = 2;
                JLabel recordingLabel = new JLabel("Initially Recording:");
                recordingLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
                panel.add(recordingLabel, gbc);
                gbc.gridx = 1;
                JCheckBox recordingCheck = new JCheckBox();
                panel.add(recordingCheck, gbc);
                break;
        }
        panel.revalidate();
        panel.repaint();
    }

    private Device createDevice(String typeName, String name, JPanel additionalFieldsPanel) {
        try {
            switch (typeName) {
                case "Light":
                    Light light = new Light(name);
                    Component[] components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JSpinner && i == 1) {
                            int brightness = (Integer) ((JSpinner) components[i]).getValue();
                            light.setBrightness(brightness);
                        }
                        if (components[i] instanceof JComboBox && i == 3) {
                            String color = (String) ((JComboBox<?>) components[i]).getSelectedItem();
                            light.setColor(color);
                        }
                    }
                    return light;
                case "Fan":
                    Fan fan = new Fan(name);
                    components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JSpinner && i == 1) {
                            int speed = (Integer) ((JSpinner) components[i]).getValue();
                            fan.setSpeed(speed);
                        }
                        if (components[i] instanceof JCheckBox && i == 3) {
                            boolean oscillating = ((JCheckBox) components[i]).isSelected();
                            if (oscillating) {
                                fan.toggleOscillation();
                            }
                        }
                    }
                    return fan;
                case "AirConditioner":
                    AirConditioner ac = new AirConditioner(name);
                    components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JSpinner && i == 1) {
                            int temp = (Integer) ((JSpinner) components[i]).getValue();
                            ac.setTemperature(temp);
                        }
                        if (components[i] instanceof JComboBox && i == 3) {
                            String mode = (String) ((JComboBox<?>) components[i]).getSelectedItem();
                            ac.setMode(mode);
                        }
                    }
                    return ac;
                case "Thermostat":
                    Thermostat thermo = new Thermostat(name);
                    components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JSpinner && i == 1) {
                            int targetTemp = (Integer) ((JSpinner) components[i]).getValue();
                            thermo.setTargetTemperature(targetTemp);
                        }
                        if (components[i] instanceof JSpinner && i == 3) {
                            int currentTemp = (Integer) ((JSpinner) components[i]).getValue();
                            thermo.setCurrentTemperature(currentTemp);
                        }
                        if (components[i] instanceof JComboBox && i == 5) {
                            String mode = (String) ((JComboBox<?>) components[i]).getSelectedItem();
                            if (!"off".equals(mode)) {
                                thermo.turnOn();
                                thermo.setMode(mode);
                            }
                        }
                    }
                    return thermo;
                case "DoorLock":
                    String location = "Front Door";
                    boolean initiallyLocked = true;
                    components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JTextField && i == 1) {
                            location = ((JTextField) components[i]).getText();
                        }
                        if (components[i] instanceof JCheckBox && i == 3) {
                            initiallyLocked = ((JCheckBox) components[i]).isSelected();
                        }
                    }
                    DoorLock doorLock = new DoorLock(name, location);
                    if (!initiallyLocked) {
                        doorLock.turnOff(); // Unlock
                    }
                    return doorLock;
                case "SecurityCamera":
                    String camLocation = "Living Room";
                    int resolution = 1080;
                    boolean initiallyRecording = false;
                    components = additionalFieldsPanel.getComponents();
                    for (int i = 0; i < components.length; i++) {
                        if (components[i] instanceof JTextField && i == 1) {
                            camLocation = ((JTextField) components[i]).getText();
                        }
                        if (components[i] instanceof JComboBox && i == 3) {
                            String resStr = (String) ((JComboBox<?>) components[i]).getSelectedItem();
                            if ("4K".equals(resStr)) resolution = 2160;
                            else if ("720".equals(resStr)) resolution = 720;
                            else resolution = 1080;
                        }
                        if (components[i] instanceof JCheckBox && i == 5) {
                            initiallyRecording = ((JCheckBox) components[i]).isSelected();
                        }
                    }
                    SecurityCamera camera = new SecurityCamera(name, camLocation);
                    camera.setResolution(resolution);
                    if (initiallyRecording) {
                        camera.turnOn();
                        camera.toggleRecording();
                    }
                    return camera;
                default:
                    return null;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error creating device: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    private JPanel createLogPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(BACKGROUND_COLOR);

        // Title
        JLabel titleLabel = new JLabel("📝 System Log");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logArea.setText("System Log Started\n------------------\n");
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_COLOR, 1));
        panel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton clearButton = new JButton("🧹 Clear Log");
        styleButton(clearButton, Color.GRAY);
        clearButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logArea.setText("System Log Started\n------------------\n");
                appendToLog("Log cleared");
            }
        });
        buttonPanel.add(clearButton);

        JButton saveButton = new JButton("💾 Save Data");
        styleButton(saveButton, SUCCESS_COLOR);
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                DevicePersistenceManager.saveDevices();
                appendToLog("Devices and tasks saved to file.");
                JOptionPane.showMessageDialog(SmartHomeGUI.this, "Data saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);

        JButton loadButton = new JButton("📂 Load Data");
        styleButton(loadButton, PRIMARY_COLOR);
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    SmartHomeGUI.this,
                    "Loading will replace current devices and tasks. Continue?",
                    "Confirm Load",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    // Clear current data
                    controller.getDevices().clear();
                    controller.getScheduledTasks().clear();
                    // Load saved data
                    DevicePersistenceManager.loadDevices();
                    refreshDeviceTable();
                    refreshTaskTable();
                    refreshDashboardStatus(dashboardPanel);
                    appendToLog("Devices and tasks loaded from file.");
                    JOptionPane.showMessageDialog(SmartHomeGUI.this, "Data loaded successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        buttonPanel.add(loadButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadData() {
        // Add some sample devices for demonstration
        if (controller.getDevices().isEmpty()) {
            controller.addDevice(new Light("Living Room Light"));
            controller.addDevice(new Light("Bedroom Light"));
            controller.addDevice(new Fan("Ceiling Fan"));
            controller.addDevice(new AirConditioner("Main AC"));
            controller.addDevice(new Thermostat("Main Thermostat"));
            controller.addDevice(new DoorLock("Front Door", "Main Entrance"));
            controller.addDevice(new SecurityCamera("Front Camera", "Front Door"));

            // Add sample scheduled task
            ScheduledTask task = new ScheduledTask("Living Room Light", "turn on", "19:00", true);
            controller.addScheduledTask(task);
            appendToLog("Sample devices and tasks added for demonstration.");
        }
        refreshDeviceTable();
        refreshTaskTable();
    }

    // Custom cell renderer and editor for buttons in tables
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setBackground(ACCENT_COLOR);
            setForeground(Color.WHITE);
            setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int currentRow;
        private JTable currentTable;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentTable = table;
            currentRow = row;
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            button.setBackground(ACCENT_COLOR);
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if ("Manage".equals(label)) {
                SwingUtilities.invokeLater(() -> {
                    if (currentTable == taskTable) {
                        manageTask(currentRow);
                    }
                    refreshDashboardStatus(dashboardPanel);
                });
            } else if ("Toggle".equals(label)) {
                SwingUtilities.invokeLater(() -> {
                    if (currentTable == taskTable) {
                        toggleTaskEnabled(currentRow);
                    }
                });
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        @Override
        protected void fireEditingStopped() {
            super.fireEditingStopped();
        }
    }

    // Custom cell renderer and editor for checkboxes
    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
        public CheckBoxRenderer() {
            setOpaque(true);
            setHorizontalAlignment(SwingConstants.CENTER);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof Boolean) {
                setSelected((Boolean) value);
            } else {
                setSelected(false);
            }
            return this;
        }
    }

    class CheckBoxEditor extends DefaultCellEditor {
        private JTable currentTable;
        private int currentRow;

        public CheckBoxEditor() {
            super(new JCheckBox());
            ((JCheckBox) editorComponent).setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentTable = table;
            currentRow = row;
            JCheckBox checkBox = (JCheckBox) editorComponent;
            checkBox.setSelected((value instanceof Boolean) ? (Boolean) value : false);
            return checkBox;
        }

        @Override
        public Object getCellEditorValue() {
            JCheckBox checkBox = (JCheckBox) editorComponent;
            boolean newValue = checkBox.isSelected();
            // Toggle the task's enabled state
            SwingUtilities.invokeLater(() -> {
                if (currentTable == taskTable) {
                    toggleTaskEnabled(currentRow);
                }
            });
            return newValue;
        }
    }

    // Observer implementation
    @Override
    public void update(String message) {
        appendToLog(message);
    }

    private void appendToLog(String message) {
        if (logArea != null) {
            String timestamp = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.append("[" + timestamp + "] " + message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        }
    }

    // Utility method to style buttons
    private void styleButton(JButton button, Color backgroundColor) {
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(backgroundColor);
            }
        });
    }

    // Show dialogs for device adjustments
    private void showLightDialog(Light light) {
        JDialog dialog = new JDialog(this, "Adjust Light Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Brightness slider
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel brightnessLabel = new JLabel("Brightness:");
        brightnessLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(brightnessLabel, gbc);
        gbc.gridx = 1;
        JSlider brightnessSlider = new JSlider(0, 100, light.getBrightness());
        brightnessSlider.setMajorTickSpacing(20);
        brightnessSlider.setMinorTickSpacing(10);
        brightnessSlider.setPaintTicks(true);
        brightnessSlider.setPaintLabels(true);
        brightnessSlider.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        dialog.add(brightnessSlider, gbc);

        // Color options
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel colorLabel = new JLabel("Color:");
        colorLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(colorLabel, gbc);
        gbc.gridx = 1;
        String[] colors = {"warm", "cool", "daylight"};
        JComboBox<String> colorCombo = new JComboBox<>(colors);
        colorCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        colorCombo.setSelectedItem(light.getColor());
        dialog.add(colorCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(dialog.getContentPane().getBackground());

        JButton applyButton = new JButton("Apply");
        styleButton(applyButton, SUCCESS_COLOR);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                light.setBrightness(brightnessSlider.getValue());
                light.setColor((String) colorCombo.getSelectedItem());
                refreshDashboardStatus(dashboardPanel);
                refreshDeviceTable();
                dialog.dispose();
                appendToLog("Light settings updated: " + light.getName());
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void showFanDialog(Fan fan) {
        JDialog dialog = new JDialog(this, "Adjust Fan Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Speed selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel speedLabel = new JLabel("Speed:");
        speedLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(speedLabel, gbc);
        gbc.gridx = 1;
        SpinnerModel speedModel = new SpinnerNumberModel(fan.getSpeed(), 1, 5, 1);
        JSpinner speedSpinner = new JSpinner(speedModel);
        speedSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dialog.add(speedSpinner, gbc);

        // Oscillation checkbox
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel oscillationLabel = new JLabel("Oscillating:");
        oscillationLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(oscillationLabel, gbc);
        gbc.gridx = 1;
        JCheckBox oscillationCheck = new JCheckBox();
        oscillationCheck.setSelected(fan.isOscillating());
        dialog.add(oscillationCheck, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(dialog.getContentPane().getBackground());

        JButton applyButton = new JButton("Apply");
        styleButton(applyButton, SUCCESS_COLOR);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fan.setSpeed((Integer) speedSpinner.getValue());
                if (oscillationCheck.isSelected() != fan.isOscillating()) {
                    fan.toggleOscillation();
                }
                refreshDashboardStatus(dashboardPanel);
                refreshDeviceTable();
                dialog.dispose();
                appendToLog("Fan settings updated: " + fan.getName());
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void showAcDialog(AirConditioner ac) {
        JDialog dialog = new JDialog(this, "Adjust AC Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 200);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Temperature selection
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel tempLabel = new JLabel("Temperature:");
        tempLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(tempLabel, gbc);
        gbc.gridx = 1;
        SpinnerModel tempModel = new SpinnerNumberModel(ac.getTemperature(), 16, 30, 1);
        JSpinner tempSpinner = new JSpinner(tempModel);
        tempSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dialog.add(tempSpinner, gbc);

        // Mode selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(modeLabel, gbc);
        gbc.gridx = 1;
        String[] modes = {"cool", "heat", "fan", "dry"};
        JComboBox<String> modeCombo = new JComboBox<>(modes);
        modeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        modeCombo.setSelectedItem(ac.getMode());
        dialog.add(modeCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(dialog.getContentPane().getBackground());

        JButton applyButton = new JButton("Apply");
        styleButton(applyButton, SUCCESS_COLOR);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ac.setTemperature((Integer) tempSpinner.getValue());
                ac.setMode((String) modeCombo.getSelectedItem());
                refreshDashboardStatus(dashboardPanel);
                refreshDeviceTable();
                dialog.dispose();
                appendToLog("AC settings updated: " + ac.getName());
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }

    private void showThermostatDialog(Thermostat thermostat) {
        JDialog dialog = new JDialog(this, "Adjust Thermostat Settings", true);
        dialog.setLayout(new GridBagLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(SECONDARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Target temperature
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel targetLabel = new JLabel("Target Temp:");
        targetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(targetLabel, gbc);
        gbc.gridx = 1;
        SpinnerModel targetModel = new SpinnerNumberModel(thermostat.getTargetTemperature(), 10, 35, 1);
        JSpinner targetSpinner = new JSpinner(targetModel);
        targetSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dialog.add(targetSpinner, gbc);

        // Current temperature (simulated)
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel currentLabel = new JLabel("Current Temp:");
        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(currentLabel, gbc);
        gbc.gridx = 1;
        SpinnerModel currentModel = new SpinnerNumberModel(thermostat.getCurrentTemperature(), -10, 50, 1);
        JSpinner currentSpinner = new JSpinner(currentModel);
        currentSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dialog.add(currentSpinner, gbc);

        // Mode selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel modeLabel = new JLabel("Mode:");
        modeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        dialog.add(modeLabel, gbc);
        gbc.gridx = 1;
        String[] modes = {"heat", "cool", "off"};
        JComboBox<String> modeCombo = new JComboBox<>(modes);
        modeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        modeCombo.setSelectedItem(thermostat.getMode());
        dialog.add(modeCombo, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.setBackground(dialog.getContentPane().getBackground());

        JButton applyButton = new JButton("Apply");
        styleButton(applyButton, SUCCESS_COLOR);
        applyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                thermostat.setTargetTemperature((Integer) targetSpinner.getValue());
                thermostat.setCurrentTemperature((Integer) currentSpinner.getValue());
                if (!modeCombo.getSelectedItem().equals(thermostat.getMode())) {
                    thermostat.turnOff(); // Reset
                    if (!"off".equals(modeCombo.getSelectedItem())) {
                        thermostat.turnOn();
                        thermostat.setMode((String) modeCombo.getSelectedItem());
                    }
                }
                refreshDashboardStatus(dashboardPanel);
                refreshDeviceTable();
                dialog.dispose();
                appendToLog("Thermostat settings updated: " + thermostat.getName());
            }
        });

        JButton cancelButton = new JButton("Cancel");
        styleButton(cancelButton, Color.GRAY);
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });

        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);

        dialog.add(buttonPanel, gbc);
        dialog.setVisible(true);
    }
}