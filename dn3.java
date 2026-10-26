import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

public class dn3 {

    static String readFile(String filename) throws IOException {
        return Files.readString(Path.of(filename));
    }

    static void writeFile(String filename, String content) throws IOException {
        Files.writeString(Path.of(filename), content);
    }

    //naloga 1
    public static int[] zakodiraj(int[] sporocilo, int[][] G) {  //kodira z matriko G
        int[] zakodirano = new int[31];
        for (int j = 0; j < 31; j++) {
            int vsota = 0;
            for (int i = 0; i < 26; i++) {
                vsota += sporocilo[i] * G[i][j];
            }
            zakodirano[j] = vsota % 2;
        }
        return zakodirano;
    }
    
    public static String zakodirajCrko(char c) {  //generira prevedeno crko
        String abeceda = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.,¶?'_";
        int vrednost = abeceda.indexOf(c);
        if (vrednost == -1) {
            throw new IllegalArgumentException("Neznan znak: " + c);
        }
        return String.format("%5s", Integer.toBinaryString(vrednost))
                .replace(' ', '0');
    }

    public static String zakodirajNiz(String text) {  //generira prevedene nize po 5 crk
        StringBuilder bits = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            bits.append(zakodirajCrko(text.charAt(i)));
        }
        bits.append('0');
        return bits.toString();
    }
    
    static int[] stringToInt(String bits) {
        int[] result = new int[bits.length()];
        for (int i = 0; i < bits.length(); i++) {
            result[i] = bits.charAt(i) - '0';
        }
        return result;
    }
    static String intToString(int[] bits) {
        StringBuilder result = new StringBuilder();
        for (int bit : bits) {
            result.append(bit);
        }
        return result.toString();
    }
    static String padText(String text) { // ce besedilo ni deljivo z 5
        StringBuilder result = new StringBuilder(text);
        while (result.length() % 5 != 0) {
            result.append('_');
        }
        return result.toString();
    }
    static String zakodirajText(String text, int[][] G) {  //vhod je originalni text
        text = text.replace(' ', '_');
        text = padText(text);
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < text.length(); i += 5) {  //pomik po blokih
            String group = text.substring(i, i + 5);
            String zakodirano26 = zakodirajNiz(group);  //prevajanje po blokih
            int[] sporocilo = stringToInt(zakodirano26);  //pretvorba
            int[] zakodirano = zakodiraj(sporocilo, G); //kodiranje z G
            result.append(intToString(zakodirano));  //pretvorba
        }
        return result.toString();
    }
    //konec naloge 1

    //naloga 2
    static int[] getSyndrome(int[] received, int[][] H) {  // vrne sindrom
        int[] syndrome = new int[5];
        for (int i = 0; i < 5; i++) {
            int sum = 0;
            for (int j = 0; j < 31; j++) {
                sum += H[i][j] * received[j];
            }
            syndrome[i] = sum % 2;
        }
        return syndrome;
    }

    static int findErrorPosition(int[] syndrome, int[][] H) {  //vrne lokacijo napake
        boolean pravilno = true;  //preverimo ce ni napake
        for (int bit : syndrome) {
            if (bit != 0) {
                pravilno = false;
                break;
            }
        }
        if (pravilno) {
            return -1;
        }

        for (int column = 0; column < 31; column++) {  //ce je napaka, najdemo kje
            boolean equal = true;
            for (int row = 0; row < 5; row++) {
                if (H[row][column] != syndrome[row]) {
                    equal = false;
                    break;
                }
            }
            if (equal) {
                return column;
            }
        }
        return -1;
    }
    static int[] correctCodeword(int[] received, int[][] H) {  //popravimo
        int[] syndrome = getSyndrome(received, H);
        int errorPosition = findErrorPosition(syndrome, H);
        if (errorPosition != -1) {
            received[errorPosition] = 1 - received[errorPosition];
        }
        return received;
    }
    static char decodeCharacter(int[] bits, int start) {  // dekodiranje po zankih oz. slovar
        int value = 0;
        for (int i = 0; i < 5; i++) {
            value = value * 2 + bits[start + i];
        }
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ.,¶?'_";
        return alphabet.charAt(value);
    }
    static String decodeMessage(int[] message) {  //dekodiranje po blokih
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < 25; i += 5) {
            result.append(decodeCharacter(message, i));  //dekodiranje po znakih
        }
        return result.toString();
    }
    static String decodeText(String encoded, int[][] H) {  //dekodiranje celotnega kodnega besedila
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < encoded.length(); i += 31) {  //pomik po blokih
            String block = encoded.substring(i, i + 31);
            int[] received = stringToInt(block);  //pretvorba
            int[] message = correctCodeword(received, H); //popravimo napako
            result.append(decodeMessage(message));  //dekodiranje po blokih v koncno besedilo
        }
        return result.toString();
    }

    public static void main(String args[]) throws IOException {
        int[][] H = {
                        {1, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 0, 1, 1, 0, 0, 0, 0},
                        {1, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0},
                        {0, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 0},
                        {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 1}
                    };
        //konstrukcija G
        int[][] G = new int[26][31];
        for (int i = 0; i < 26; i++) {
            G[i][i] = 1;
        }
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 5; j++) {
                G[i][26 + j] = H[j][i];
            }
        }

        // naloga 1
        String text = readFile("ime.txt");
        String zakodirano = zakodirajText(text, G);
        writeFile("kodiranoime.txt", zakodirano);
        // konec 1

        //naloga 2
        String encoded = readFile("kodirano1.txt");
        String decoded = decodeText(encoded, H);
        writeFile("besedilo1.txt", decoded);

        String encoded2 = readFile("kodirano2.txt");
        String decoded2 = decodeText(encoded2, H);
        writeFile("besedilo2.txt", decoded2);
        //naloga 2 konec
    }
}
