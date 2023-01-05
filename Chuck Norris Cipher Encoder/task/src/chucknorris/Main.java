package chucknorris;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String line;
        boolean isExit = false;
        boolean error = false;
        String out = null;
        while (!isExit) {
            System.out.println("Please input operation (encode/decode/exit):");
            String word = scanner.nextLine();
            switch (word.toLowerCase()) {
                case "encode":
                    System.out.println("Input string:");
                    line = scanner.nextLine();
                    System.out.println("Encoded string:");
                    out = chuckNorrisEncode(line, false);
                    break;
                case "decode":
                    System.out.println("Input encoded string:");
                    line = scanner.nextLine();
                    if (!chuckNorrisDecodeVerifyArgs(line)) {
                        System.out.println("Encoded string is not valid.");
                        error = true;
                    } else {
                        out = chuckNorrisDecode(line, line.length());
                        String binary = chuckNorrisEncode(out, true);
                        if (binary.length() % 7 != 0) {
                            System.out.println("Encoded string is not valid.");
                            error = true;
                        } else {
                            System.out.println("Decoded string:");
                        }
                    }
                    break;
                case "exit":
                    System.out.println("Bye!");
                    isExit = true;
                    break;
                default:
                    System.out.printf("There is no '%s' operation\n", word);
                    error = true;
                    break;
            }
            if (!isExit && !error) {
                System.out.println(out);
                System.out.println();
            }
            error = false;
        }
    }

    static boolean chuckNorrisDecodeVerifyArgs(String args) {
        String[] c = args.split("");
        String[] c1 = args.split(" ");
        // check if online 0 or " " spaces in encoded message
        long num = 0;
        boolean ok = false;
        for (int i = 0; i < args.length(); i++) {
            if (c[i].equals(" ")) {
                num++;
            } else if (c[i].equals("0")) {
                num++;
            }
        }
        ok = num == args.length();
        if (ok) {
            num = 0;
            // check if the encoded message starts with 0 or 00
            for (int i = 0; i < c1.length; i++) {
                if (c1[i].equals("0") && (i % 2) != 0) {
                    num++;
                } else if (c1[i].equals("00") && (i % 2) != 0) {
                    num++;
                }
            }

            ok = num != args.split(" ").length / 2;
            if (ok) {
                // check if number of blocks are even, if not, error.
                ok = args.split(" ").length % 2 == 0;
            }
            return ok;
        } else {
            return false;
        }
    }

    // output decoded string by grouping in 7 bits
    static String chuckNorrisDecode(String line) {

        StringBuilder groups = new StringBuilder();
        StringBuilder finalOut = new StringBuilder();
        long decimalValue = 0;
        int bCounter = 0;

        // split bits into 7 bits groups
        for (String b : line.split("")) {
            if (bCounter < 7) {
                groups.append(b);
            } else {
                groups.append(" ");
                groups.append(b);
                bCounter = 0;
            }
            bCounter++;
        }

        // get decimal value for each group of 7 bits
        for (String s : groups.toString().split(" ")) {
            decimalValue = chuckNorrisDecodeBinarytoDecimal(s);
            char c = (char) decimalValue;
            finalOut.append(c);
        }
        return finalOut.toString();
    }

    // decoding process
    static String chuckNorrisDecode(String line, int length) {
        String[] chars = line.split("");
        int bitsCounter = 0;
        String bitsType = null;
        String[] bits = new String[length];
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            if (chars[i + 1].equals(" ")) {
                bitsType = "1";
                i = i + 2;
            } else if (chars[i + 1].equals("0")) {
                bitsType = "0";
                i = i + 3;
            }
            try {
                while (!chars[i].equals(" ")) {
                    if (Objects.equals(bits[bitsCounter], null)) {
                        bits[bitsCounter] = bitsType;
                    } else {
                        bits[bitsCounter] = bits[bitsCounter] + bitsType;
                    }
                    if (i == line.length() - 1) {
                        break;
                    }
                    i++;
                }
            } catch (Exception e) {

            }
            bitsCounter++;
        }
        for (String b : bits) {
            if (!Objects.equals(b, null)) {
                out.append(b);
            }
        }
        return chuckNorrisDecode(out.toString());
    }

    // decode from binary to decimal
    static long chuckNorrisDecodeBinarytoDecimal(String line) {
        String[] bits = line.split("");
        int bCounter = bits.length - 1;
        long sum = 0;
        for (String bit : bits) {
            if (bit.equals("1")) {
                sum += (long) Math.pow(2, bCounter);
            }
            bCounter--;
        }
        return sum;
    }

    // get chars values in 1 and 0 bits in one string.
    static String chuckNorrisEncode(String line, boolean binary) {
        String[] chars = line.split("");
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < line.length(); i++) {
            int c = chars[i].charAt(0);
            String s = chuckNorrisEncodeSevenBitRepresentation(Integer.toBinaryString(c));
            out.append(s);
        }
        if (binary) {
            return out.toString();
        } else {
            return chuckNorrisEncode(out.toString(), out.length());
        }
    }

    // return encoding
    static String chuckNorrisEncode(String binaryValue, int length) {
        String[] temp = new String[length];
        StringBuilder out = new StringBuilder();

        // get groups of bits
        chuckNorrisEncodeGetGroups(temp, binaryValue);

        for (int i = 0; i < temp.length; i++) {
            if (!Objects.equals(temp[i], null)) {
                if (temp[i].charAt(0) == '1') {
                    String replace = temp[i].replace('1', '0');
                    if (i == temp.length - 1) {
                        out.append("0 " + replace);
                    } else {
                        out.append("0 " + replace + " ");
                    }
                } else {
                    if (i == temp.length - 1) {
                        out.append("00 " + temp[i]);
                    } else {
                        out.append("00 " + temp[i] + " ");
                    }
                }
            }
        }
        return out.toString();
    }

    static void chuckNorrisEncodeGetGroups(String[] temp, String binaryValue) {
        String[] nums = binaryValue.split("");
        boolean start = false;
        String tempValue = null;
        int tempStart = 0;
        for (int i = 0; i < binaryValue.length(); i++) {
            if (!start) {
                tempValue = nums[i];
                start = true;
                temp[tempStart] = nums[i];
            } else {
                if (nums[i].equals(tempValue)) {
                    temp[tempStart] = temp[tempStart] + nums[i];
                } else {
                    tempValue = nums[i];
                    tempStart++;
                    if (Objects.equals(temp[tempStart], null)) {
                        temp[tempStart] = nums[i];
                    } else {
                        temp[tempStart] = temp[tempStart] + nums[i];
                    }
                }
            }
        }
    }

    // add 0's to the left of number, so it will have 7-bit format. (if needed)
    static String chuckNorrisEncodeSevenBitRepresentation(String binaryValue) {
        String output = binaryValue;
        int length = binaryValue.length();
        if (length < 7 && length >= 5) {
            int dif = 7 - length;
            for (int i = 0; i < dif; i++) {
                output = 0 + output;
            }
        } else {
            return binaryValue;
        }
        return output;
    }
}