package encryptdecrypt;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

abstract class CodingAlgorithm {
    abstract String handleText(String text, int key);
}

class ShiftAlgorithm extends CodingAlgorithm {
    @Override
    String handleText(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            if (ch > 64 && ch < 91) {
                ch = (char) (ch + key);
                while (ch < 65) {
                    ch += 26;
                }
                while (ch > 90) {
                    ch -= 26;
                }
            } else if (ch > 96 && ch < 123) {
                ch = (char) (ch + key);
                while (ch < 97) {
                    ch += 26;
                }
                while (ch > 122) {
                    ch -= 26;
                }
            }
            result.append(ch);
        }
        return result.toString();
    }
}

class UnicodeAlgorithm extends CodingAlgorithm {
    @Override
    String handleText(String text, int key) {
        StringBuilder result = new StringBuilder();
        for (char ch : text.toCharArray()) {
            result.append((char) (ch + key));
        }
        return result.toString();
    }
}

class TextCoder {
    private String coding = "enc";
    private CodingAlgorithm algorithm = new ShiftAlgorithm();
    private StringBuilder result = new StringBuilder();
    private String text = "";
    private int key = 0;
    private boolean data = false;
    private boolean save = false;
    private String fileForSave;

    public void readFile(String fileName) {
        File file = new File(fileName);
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) {
                this.text = sc.nextLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error. File not found.");
        }
    }

    public void saveFile(String fileName) {
        this.fileForSave = fileName;
        this.save = true;
    }

    public void saveData() {
        File file = new File(fileForSave);
        try (FileWriter writer = new FileWriter(file)) {
            writer.append(result);
        } catch (IOException e) {
            System.out.println("Error. File not found.");
        }
    }

    public void setText(String text) {
        this.text = text;
        this.data = true;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public boolean hasData() {
        return data;
    }

    public void setCoding(String coding) {
        this.coding = coding;
    }

    public void setAlgorithm(String algorithm) {
        if ("shift".equals(algorithm)) {
            this.algorithm = new ShiftAlgorithm();
        } else if ("unicode".equals(algorithm)) {
            this.algorithm = new UnicodeAlgorithm();
        }
    }

    public StringBuilder handleText() {
        return new StringBuilder(this.algorithm.handleText(text, key));
    }

    public void processData() {
        if ("dec".equals(coding)) {
            key = -key;
        }
        result = handleText();
        if (save) {
            saveData();
        } else {
            System.out.println(result);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        TextCoder textCoder = new TextCoder();
        for (int i = 0; i < args.length; i += 2) {
            switch (args[i]) {
                case "-mode":
                    textCoder.setCoding(args[i + 1]);
                    break;
                case "-key":
                    textCoder.setKey(Integer.parseInt(args[i + 1]));
                    break;
                case "-data":
                    textCoder.setText(args[i + 1]);
                    break;
                case "-in":
                    if (!textCoder.hasData()) {
                        textCoder.readFile(args[i + 1]);
                    }
                    break;
                case "-out":
                    textCoder.saveFile(args[i + 1]);
                    break;
                case "-alg":
                    textCoder.setAlgorithm(args[i + 1]);
                    break;
                default:
                    break;
            }
        }
        textCoder.processData();
    }
}
