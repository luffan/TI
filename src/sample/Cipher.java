package sample;

import java.util.ArrayList;

abstract class Cipher {
    protected String key;
    protected String text;
    protected ArrayList<Character> alphabet;



    public Cipher(ArrayList<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setText(String text) {
        this.text = text;
    }

    public abstract String isValidDate();

    public void transform() {
        String lowerText = text.toLowerCase();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            if (alphabet.contains(lowerText.charAt(i))) {
                result.append(text.charAt(i));
            }
        }
        text = result.toString();
    }

    public abstract String decrypt();

    public abstract String encrypt();
}

class RealFence extends Cipher {

    public RealFence(ArrayList<Character> alphabet) {
        super(alphabet);
    }

    @Override
    public String  isValidDate() {
        StringBuilder errorMessage = new StringBuilder();
        if (text == null || text.length() == 0) {
            errorMessage.append("There are no characters in the input field that can be encrypted\n");
        }
        if (!key.matches("\\d+") || Integer.parseInt(key) < 2 || Integer.parseInt(key) >= text.length()) {
            errorMessage.append("Not a valid key");
        }
        return errorMessage.toString();
    }

    @Override
    public String decrypt() {
        int key = Integer.parseInt(this.key);
        Character[][] rail = new Character[key][text.length()];

        boolean dir_down = false;

        int row = 0, col = 0;

        for (int i = 0; i < text.length(); i++) {
            if (row == 0) {
                dir_down = true;
            }

            if (row == key - 1) {
                dir_down = false;
            }
            rail[row][col++] = '*';

            if (dir_down) {
                row++;
            } else {
                row--;
            }
        }

        int index = 0;

        for (int i = 0; i < key; i++) {
            for (int j = 0; j < text.length(); j++) {
                if (rail[i][j] != null && index < text.length()) {
                    rail[i][j] = text.charAt(index++);
                }
            }
        }

        StringBuilder result = new StringBuilder();

        row = 0;
        col = 0;

        for (int i = 0; i < text.length(); i++) {

            if (row == 0) dir_down = true;

            if (row == key - 1) dir_down = false;

            if (rail[row][col] != null) result.append(rail[row][col++]);

            if (dir_down) {
                row++;
            } else {
                row--;
            }
        }

        return result.toString();
    }

    @Override
    public String encrypt() {
        int key = Integer.parseInt(this.key);
        Character[][] rail = new Character[key][text.length()];

        var dir_down = false;

        int row = 0, col = 0;
        for (int i = 0; i < text.length(); i++) {
            if (row == 0 || row == key - 1) {
                dir_down = !dir_down;
            }
            rail[row][col++] = text.charAt(i);
            if (dir_down) {
                row++;
            } else {
                row--;
            }
        }

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < key; i++) {
            for (int j = 0; j < text.length(); j++) {
                if (rail[i][j] != null) {
                    result.append(rail[i][j]);
                }
            }
        }

        return result.toString();
    }
}

class Column extends Cipher {

    public Column(ArrayList<Character> alphabet) {
        super(alphabet);
    }

    @Override
    public String isValidDate() {
        StringBuilder errorMessage = new StringBuilder();
        if (text == null || text.length() == 0) {
            errorMessage.append("There are no characters in the input field that can be encrypted!\n");
        }
        //boolean flag = !key.matches("[a-zA-z]+");
        if (!key.matches("[a-zA-z]+") || key.length() == 1) {
            errorMessage.append("Not a valid key");
        }
        return errorMessage.toString();
    }


    @Override
    public String decrypt() {
        int row = text.length() / key.length() + 1;
        Character[][] rail = new Character[row][key.length()];

        int[] sequence = createSequence(key, alphabet);

        int index = 0;
        for (int i = 0; i < rail.length; i++) {
            for (int j = 0; j < rail[i].length; j++) {
                rail[i][j] = '*';
                index++;
                if (index == text.length()) {
                    break;
                }
            }
            if (index == text.length()) {
                break;
            }
        }
        index = 0;
        for (int i = 0; i < sequence.length; i++) {
            int indexSequence = minEl(sequence);
            sequence[indexSequence] = 1000;
            for (int j = 0; j < rail.length; j++) {
                if (rail[j][indexSequence] == null) {
                    continue;
                }
                rail[j][indexSequence] = text.charAt(index++);
            }
        }

        StringBuilder sb = new StringBuilder();
        index = 0;
        for (int i = 0; i < rail.length; i++) {
            for (int j = 0; j < rail[i].length; j++) {
                sb.append(rail[i][j]);
                index++;
                if (index == text.length()) {
                    break;
                }
            }
            if (index == text.length()) {
                break;
            }
        }
        return sb.toString();
    }

    @Override
    public String encrypt() {
        int row = (text.length() / key.length()) + 1;
        Character[][] rail = new Character[row][key.length()];

        int[] sequence = createSequence(key, alphabet);
        int index = 0;
        for (int i = 0; i < rail.length; i++) {
            for (int j = 0; j < rail[i].length; j++) {
                rail[i][j] = text.charAt(index++);
                if (index == text.length()) {
                    break;
                }
            }
            if (index == text.length()) {
                break;
            }
        }

        StringBuilder sb = new StringBuilder();
        index = 0;
        for (int i = 0; i < sequence.length; i++) {
            int min = minEl(sequence);
            sequence[min] = 34;
            for (int j = 0; j < rail.length; j++) {
                if (rail[j][min] != null) {
                    sb.append(rail[j][min]);
                }
            }
        }
        return sb.toString();
    }

    private static int minEl(int[] sequence) {
        var index = 0;
        for (var i = 1; i < sequence.length; i++) {
            if (sequence[index] > sequence[i]) {
                index = i;
            }
        }
        return index;
    }

    private static int[] createSequence(String key, ArrayList<Character> alphabet) {
        String temp = key.toLowerCase();
        int[] sequence = new int[key.length()];
        for (var i = 0; i < key.length(); i++) {
            sequence[i] = alphabet.indexOf(temp.charAt(i));
        }
        return sequence;
    }
}

class Vigenera extends Cipher {

    public Vigenera(ArrayList<Character> alphabet) {
        super(alphabet);
    }

    @Override
    public String isValidDate() {
        StringBuilder errorMessage = new StringBuilder();
        if (text == null || text.length() == 0) {
            errorMessage.append("There are no characters in the input field that can be encrypted!\n");
        }
        if (!check(key)) {
            errorMessage.append("Not a valid key");
        }
        return errorMessage.toString();
    }

    private boolean check(String key) {
        for(int i = 0; i < key.length(); i++) {
            if (!(key.charAt(i) >= 'а' && key.charAt(i) <= 'я' || key.charAt(i) >= 'А' && key.charAt(i) <= 'Я')) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String decrypt() {
        StringBuilder result = new StringBuilder();
        String inputLower = text.toLowerCase();
        String keywordLower = key.toLowerCase();
        int keyword_index = 0;

        for (int i = 0; i < text.length(); i++) {

            int c = (alphabet.indexOf(inputLower.charAt(i)) + alphabet.size() -
                    alphabet.indexOf(keywordLower.charAt(keyword_index))) % alphabet.size();

            if (Character.isLowerCase(text.charAt(i)))
                result.append(alphabet.get(c));
            else
                result.append(Character.toUpperCase(alphabet.get(c)));

            keyword_index++;

            if (keyword_index == key.length())
                keyword_index = 0;
        }

        return result.toString();
    }

    @Override
    public String encrypt() {
        String inputLower = text.toLowerCase();
        String keywordLower = key.toLowerCase();
        StringBuilder result = new StringBuilder();

        int keyword_index = 0;

        for (int i = 0; i < text.length(); i++) {
            int c = (alphabet.indexOf(inputLower.charAt(i)) + alphabet.indexOf(keywordLower.charAt(keyword_index))) % alphabet.size();
            if (Character.isLowerCase(text.charAt(i)))
                result.append(alphabet.get(c));
            else
                result.append(Character.toUpperCase(alphabet.get(c)));

            keyword_index++;

            if (keyword_index == key.length())
                keyword_index = 0;
        }

        return result.toString();
    }
}
