import java.awt.*;
import java.io.*;
import java.util.Scanner;
import javax.swing.*;

public class AIChatBot {

    static JTextField userInput;
    static JPanel chatPanel;
    static JScrollPane scrollPane;

    public static void main(String[] args) {

        // FRAME
        JFrame frame = new JFrame("CHITTI AI");
        frame.setSize(400, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // CHAT PANEL
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        scrollPane = new JScrollPane(chatPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        // BOTTOM PANEL
        JPanel bottomPanel = new JPanel(new BorderLayout());
        userInput = new JTextField();
        JButton sendBtn = new JButton("Send");
        JButton clear = new JButton("clear");
        
        bottomPanel.add(clear,BorderLayout.WEST);
        bottomPanel.add(userInput, BorderLayout.CENTER);
        bottomPanel.add(sendBtn, BorderLayout.EAST);

        frame.add(bottomPanel, BorderLayout.SOUTH);

        // LOAD OLD CHAT
        loadHistory();

        // SEND BUTTON ACTION
        sendBtn.addActionListener(e -> sendMessage());
        
        // CLEAR BUTTON ACTION
        clear.addActionListener(e -> clearHistory());

        frame.setVisible(true);
    }

    // SEND MESSAGE
    static void sendMessage() {
        String message = userInput.getText().trim();
        if (message.isEmpty()) return;

        addMessage("You: " + message, FlowLayout.RIGHT);
        saveHistory("You: " + message);

        userInput.setText("");

        // GET AI RESPONSE
        String response = getAIResponse(message);

        addMessage("AI: " + response, FlowLayout.LEFT);
        saveHistory("AI: " + response);
    }

    // ADD MESSAGE TO UI
    static void addMessage(String text, int align) {
        JPanel panel = new JPanel(new FlowLayout(align));
        JLabel label = new JLabel(text);
        panel.add(label);

        chatPanel.add(panel);
        chatPanel.revalidate();

        // AUTO SCROLL DOWN
        SwingUtilities.invokeLater(() ->
            scrollPane.getVerticalScrollBar().setValue(
                scrollPane.getVerticalScrollBar().getMaximum()
            )
        );
    }

    // API CALL
    static String getAIResponse(String userMsg) {

        try {

            // Escape quotes
            userMsg = userMsg
                    .replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n");

            // JSON body
            String json = """
            {
                "model": "gpt-4o-mini",
                "messages": [
                    {
                        "role": "user",
                        "content": "%s"
                    }
                ]
            }
            """.formatted(userMsg);

            // API call
            String response = API.post(
                    "https://api.openai.com/v1/chat/completions",
                    json
            );

            // Extract AI text only
            return(response);

        }

        catch (Exception e) {

            e.printStackTrace();
            return "API Error: " + e.getMessage();

        }
    }

    // SAVE HISTORY (APPEND)
    static void saveHistory(String text) {
        try {
            FileWriter fw = new FileWriter("history.txt", true);
            fw.write(text + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD HISTORY
    static void loadHistory() {
        try {
            File file = new File("history.txt");
            if (!file.exists()) return;

            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                if (line.startsWith("You:"))
                    addMessage(line, FlowLayout.RIGHT);
                else
                    addMessage(line, FlowLayout.LEFT);
            }
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //create clear history function
    static void clearHistory(){
        try {
            FileWriter fw = new FileWriter("history.txt", false);
            fw.write("");
            fw.close();
            chatPanel.removeAll();
            chatPanel.revalidate();
            chatPanel.repaint(); // refresh the chat panel
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}  