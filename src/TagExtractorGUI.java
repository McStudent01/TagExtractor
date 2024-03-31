import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class TagExtractorGUI extends JFrame {
    private JButton openFileButton, saveTagsButton, loadStopWordsButton;
    private JTextArea textArea;
    private Map<String, Integer> wordFrequency = new HashMap<>();
    private Set<String> stopWords = new HashSet<>();

    public TagExtractorGUI() {
        super("Tag Extractor");
        setLayout(new FlowLayout());

        openFileButton = new JButton("Open Text File");
        loadStopWordsButton = new JButton("Load Stop Words File");
        saveTagsButton = new JButton("Save Tags");
        textArea = new JTextArea(20, 30);
        JScrollPane scrollPane = new JScrollPane(textArea);

        add(openFileButton);
        add(loadStopWordsButton);
        add(saveTagsButton);
        add(scrollPane);

        openFileButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                scanFile(selectedFile);
            }
        });

        loadStopWordsButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                File stopWordsFile = fileChooser.getSelectedFile();
                loadStopWords(stopWordsFile);
            }
        });

        saveTagsButton.addActionListener(e -> saveTagsToFile());

        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    private void scanFile(File file) {
        wordFrequency.clear();
        textArea.setText("");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String word = scanner.next().toLowerCase();
                word = word.replaceAll("[^a-z]", ""); // Remove non-letter characters
                if (!stopWords.contains(word) && !word.isEmpty()) {
                    wordFrequency.put(word, wordFrequency.getOrDefault(word, 0) + 1);
                }
            }

            textArea.append("Tags extracted from: " + file.getName() + "\n");
            for (String key : wordFrequency.keySet()) {
                textArea.append(key + ": " + wordFrequency.get(key) + "\n");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadStopWords(File file) {
        stopWords.clear();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                stopWords.add(scanner.nextLine().trim().toLowerCase());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void saveTagsToFile() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter out = new PrintWriter(file)) {
                for (Map.Entry<String, Integer> entry : wordFrequency.entrySet()) {
                    out.println(entry.getKey() + ": " + entry.getValue());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TagExtractorGUI::new);
    }
}
