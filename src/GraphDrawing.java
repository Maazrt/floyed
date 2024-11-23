import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GraphDrawing extends JPanel {
    private List<String> nodes;
    private List<int[]> edges;
    private int[][] distance;
    private int[][] next;
    private List<Integer> highlightedPath;

    public GraphDrawing() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        setLayout(new BorderLayout());


        JPanel inputPanel = new JPanel();
        JTextField nodeInput = new JTextField(10);
        JTextField edgeInput = new JTextField(10);
        JTextField startNodeInput = new JTextField(10);
        JTextField endNodeInput = new JTextField(10);
        JButton addNodeButton = new JButton("افزودن گره");
        JButton addEdgeButton = new JButton("افزودن یال");
        JButton drawButton = new JButton("رسم گراف");
        JButton findPathButton = new JButton("یافتن مسیر کوتاه");

        inputPanel.add(new JLabel("گره:"));
        inputPanel.add(nodeInput);
        inputPanel.add(addNodeButton);
        inputPanel.add(new JLabel("یال (فرمت: A-B):"));
        inputPanel.add(edgeInput);
        inputPanel.add(addEdgeButton);
        inputPanel.add(new JLabel("شروع:"));
        inputPanel.add(startNodeInput);
        inputPanel.add(new JLabel("پایان:"));
        inputPanel.add(endNodeInput);
        inputPanel.add(findPathButton);
        inputPanel.add(drawButton);

        add(inputPanel, BorderLayout.NORTH);

        addNodeButton.addActionListener(e -> {
            String nodeName = nodeInput.getText();
            if (!nodeName.isEmpty() && !nodes.contains(nodeName)) {
                nodes.add(nodeName);
                nodeInput.setText("");
                JOptionPane.showMessageDialog(this, "گره " + nodeName + " اضافه شد.");
            } else {
                JOptionPane.showMessageDialog(this, "نام گره نامعتبر یا قبلاً وجود دارد.");
            }
        });

        addEdgeButton.addActionListener(e -> {
            String edge = edgeInput.getText();
            String[] parts = edge.split("-");
            if (parts.length == 2 && nodes.contains(parts[0]) && nodes.contains(parts[1])) {
                edges.add(new int[]{nodes.indexOf(parts[0]), nodes.indexOf(parts[1])});
                edgeInput.setText("");
                JOptionPane.showMessageDialog(this, "یال " + edge + " اضافه شد.");
            } else {
                JOptionPane.showMessageDialog(this, "فرمت یال نامعتبر یا گره‌ها وجود ندارند.");
            }
        });

        findPathButton.addActionListener(e -> {
            String startNode = startNodeInput.getText();
            String endNode = endNodeInput.getText();
            if (nodes.contains(startNode) && nodes.contains(endNode)) {
                int startIndex = nodes.indexOf(startNode);
                int endIndex = nodes.indexOf(endNode);
                calculateShortestPaths();
                highlightedPath = reconstructPath(startIndex, endIndex);
                if (highlightedPath != null) {
                    JOptionPane.showMessageDialog(this, "مسیر کوتاه: " + highlightedPath);
                } else {
                    JOptionPane.showMessageDialog(this, "مسیر وجود ندارد.");
                }
                repaint();
            } else {
                JOptionPane.showMessageDialog(this, "گره‌های نامعتبر.");
            }
        });

        drawButton.addActionListener(e -> {
            repaint();
        });
    }

    private void calculateShortestPaths() {
        int V = nodes.size();
        distance = new int[V][V];
        next = new int[V][V];


        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (i == j) {
                    distance[i][j] = 0;
                } else {
                    distance[i][j] = Integer.MAX_VALUE;
                }
                next[i][j] = -1;
            }
        }

        // Set distances for edges
        for (int[] edge : edges) {
            distance[edge[0]][edge[1]] = 1; // Assuming all edges have weight 1
            next[edge[0]][edge[1]] = edge[1];
        }

        // Floyd-Warshall algorithm
        for (int k = 0; k < V; k++) {
            for (int i = 0; i < V; i++) {
                for (int j = 0; j < V; j++) {
                    if (distance[i][k] != Integer.MAX_VALUE && distance[k][j] != Integer.MAX_VALUE) {
                        if (distance[i][j] > distance[i][k] + distance[k][j]) {
                            distance[i][j] = distance[i][k] + distance[k][j];
                            next[i][j] = next[i][k];
                        }
                    }
                }
            }
        }
    }

    private List<Integer> reconstructPath(int start, int end) {
        if (next[start][end] == -1) {
            return null; 
        }
        List<Integer> path = new ArrayList<>();
        for (int at = start; at != end; at = next[at][end]) {
            path.add(at);
        }
        path.add(end);
        return path;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (nodes.isEmpty()) {
            return;
        }

        int radius = 20;
        int angleStep = 360 / nodes.size();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        // Draw nodes
        for (int i = 0; i < nodes.size(); i++) {
            int x = (int) (centerX + 100 * Math.cos(Math.toRadians(i * angleStep)));
            int y = (int) (centerY + 100 * Math.sin(Math.toRadians(i * angleStep)));
            g.setColor(Color.BLACK);
            g.fillOval(x - radius, y - radius, radius * 2, radius * 2);


            g.setColor(Color.BLACK);
            g.drawString(nodes.get(i), x - radius / 2, y + radius + 15);
        }

        // Draw edges
        for (int[] edge : edges) {
            int x1 = (int) (centerX + 100 * Math.cos(Math.toRadians(edge[0] * angleStep)));
            int y1 = (int) (centerY + 100 * Math.sin(Math.toRadians(edge[0] * angleStep)));
            int x2 = (int) (centerX + 100 * Math.cos(Math.toRadians(edge[1] * angleStep)));
            int y2 = (int) (centerY + 100 * Math.sin(Math.toRadians(edge[1] * angleStep)));

            g.setColor(Color.GRAY);
            g.drawLine(x1, y1, x2, y2);
        }

        // Highlight the path
        if (highlightedPath != null) {
            g.setColor(Color.green);
            for (int i = 0; i < highlightedPath.size() - 1; i++) {
                int x1 = (int) (centerX + 100 * Math.cos(Math.toRadians(highlightedPath.get(i) * angleStep)));
                int y1 = (int) (centerY + 100 * Math.sin(Math.toRadians(highlightedPath.get(i) * angleStep)));
                int x2 = (int) (centerX + 100 * Math.cos(Math.toRadians(highlightedPath.get(i + 1) * angleStep)));
                int y2 = (int) (centerY + 100 * Math.sin(Math.toRadians(highlightedPath.get(i + 1) * angleStep)));

                g.setColor(Color.green );
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Graph Drawing");
        GraphDrawing graphDrawing = new GraphDrawing();
        frame.add(graphDrawing);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}