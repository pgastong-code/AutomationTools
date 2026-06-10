package keypresssimulator;

import java.awt.AWTException;
import java.awt.Robot;

public class Keyboard {
	private Robot robot;

//	public static void main(String... args) throws Exception {
//		Keyboard keyboard = new Keyboard();
//		keyboard.type("Hello there, how are you");
//	}

	public Keyboard() throws AWTException {
		this.robot = new Robot();
	}

	public Keyboard(Robot robot) {
		this.robot = robot;
	}

	public void type(CharSequence characters) {
		int length = characters.length();
		for (int i = 0; i < length; i++) {
			char character = characters.charAt(i);
			type(character);
		}
	}

	public void type(char character) {
		switch (character) {
		case 'a':
			doType(new int[] { 65 });
			return;
		case 'b':
			doType(new int[] { 66 });
			return;
		case 'c':
			doType(new int[] { 67 });
			return;
		case 'd':
			doType(new int[] { 68 });
			return;
		case 'e':
			doType(new int[] { 69 });
			return;
		case 'f':
			doType(new int[] { 70 });
			return;
		case 'g':
			doType(new int[] { 71 });
			return;
		case 'h':
			doType(new int[] { 72 });
			return;
		case 'i':
			doType(new int[] { 73 });
			return;
		case 'j':
			doType(new int[] { 74 });
			return;
		case 'k':
			doType(new int[] { 75 });
			return;
		case 'l':
			doType(new int[] { 76 });
			return;
		case 'm':
			doType(new int[] { 77 });
			return;
		case 'n':
			doType(new int[] { 78 });
			return;
		case 'o':
			doType(new int[] { 79 });
			return;
		case 'p':
			doType(new int[] { 80 });
			return;
		case 'q':
			doType(new int[] { 81 });
			return;
		case 'r':
			doType(new int[] { 82 });
			return;
		case 's':
			doType(new int[] { 83 });
			return;
		case 't':
			doType(new int[] { 84 });
			return;
		case 'u':
			doType(new int[] { 85 });
			return;
		case 'v':
			doType(new int[] { 86 });
			return;
		case 'w':
			doType(new int[] { 87 });
			return;
		case 'x':
			doType(new int[] { 88 });
			return;
		case 'y':
			doType(new int[] { 89 });
			return;
		case 'z':
			doType(new int[] { 90 });
			return;
		case 'A':
			doType(new int[] { 16, 65 });
			return;
		case 'B':
			doType(new int[] { 16, 66 });
			return;
		case 'C':
			doType(new int[] { 16, 67 });
			return;
		case 'D':
			doType(new int[] { 16, 68 });
			return;
		case 'E':
			doType(new int[] { 16, 69 });
			return;
		case 'F':
			doType(new int[] { 16, 70 });
			return;
		case 'G':
			doType(new int[] { 16, 71 });
			return;
		case 'H':
			doType(new int[] { 16, 72 });
			return;
		case 'I':
			doType(new int[] { 16, 73 });
			return;
		case 'J':
			doType(new int[] { 16, 74 });
			return;
		case 'K':
			doType(new int[] { 16, 75 });
			return;
		case 'L':
			doType(new int[] { 16, 76 });
			return;
		case 'M':
			doType(new int[] { 16, 77 });
			return;
		case 'N':
			doType(new int[] { 16, 78 });
			return;
		case 'O':
			doType(new int[] { 16, 79 });
			return;
		case 'P':
			doType(new int[] { 16, 80 });
			return;
		case 'Q':
			doType(new int[] { 16, 81 });
			return;
		case 'R':
			doType(new int[] { 16, 82 });
			return;
		case 'S':
			doType(new int[] { 16, 83 });
			return;
		case 'T':
			doType(new int[] { 16, 84 });
			return;
		case 'U':
			doType(new int[] { 16, 85 });
			return;
		case 'V':
			doType(new int[] { 16, 86 });
			return;
		case 'W':
			doType(new int[] { 16, 87 });
			return;
		case 'X':
			doType(new int[] { 16, 88 });
			return;
		case 'Y':
			doType(new int[] { 16, 89 });
			return;
		case 'Z':
			doType(new int[] { 16, 90 });
			return;
		case '`':
			doType(new int[] { 192 });
			return;
		case '0':
			doType(new int[] { 48 });
			return;
		case '1':
			doType(new int[] { 49 });
			return;
		case '2':
			doType(new int[] { 50 });
			return;
		case '3':
			doType(new int[] { 51 });
			return;
		case '4':
			doType(new int[] { 52 });
			return;
		case '5':
			doType(new int[] { 53 });
			return;
		case '6':
			doType(new int[] { 54 });
			return;
		case '7':
			doType(new int[] { 55 });
			return;
		case '8':
			doType(new int[] { 56 });
			return;
		case '9':
			doType(new int[] { 57 });
			return;
		case '-':
			doType(new int[] { 45 });
			return;
		case '=':
			doType(new int[] { 61 });
			return;
		case '~':
			doType(new int[] { 16, 192 });
			return;
		case '!':
			doType(new int[] { 517 });
			return;
		case '@':
			doType(new int[] { 512 });
			return;
		case '#':
			doType(new int[] { 520 });
			return;
		case '$':
			doType(new int[] { 515 });
			return;
		case '%':
			doType(new int[] { 16, 53 });
			return;
		case '^':
			doType(new int[] { 514 });
			return;
		case '&':
			doType(new int[] { 150 });
			return;
		case '*':
			doType(new int[] { 151 });
			return;
		case '(':
			doType(new int[] { 519 });
			return;
		case ')':
			doType(new int[] { 522 });
			return;
		case '_':
			doType(new int[] { 523 });
			return;
		case '+':
			doType(new int[] { 521 });
			return;
		case '\t':
			doType(new int[] { 9 });
			return;
		case '\n':
			doType(new int[] { 10 });
			return;
		case '[':
			doType(new int[] { 91 });
			return;
		case ']':
			doType(new int[] { 93 });
			return;
		case '\\':
			doType(new int[] { 92 });
			return;
		case '{':
			doType(new int[] { 16, 91 });
			return;
		case '}':
			doType(new int[] { 16, 93 });
			return;
		case '|':
			doType(new int[] { 16, 92 });
			return;
		case ';':
			doType(new int[] { 59 });
			return;
		case ':':
			doType(new int[] { 513 });
			return;
		case '\'':
			doType(new int[] { 222 });
			return;
		case '"':
			doType(new int[] { 152 });
			return;
		case ',':
			doType(new int[] { 44 });
			return;
		case '<':
			doType(new int[] { 16, 44 });
			return;
		case '.':
			doType(new int[] { 46 });
			return;
		case '>':
			doType(new int[] { 16, 46 });
			return;
		case '/':
			doType(new int[] { 47 });
			return;
		case '?':
			doType(new int[] { 16, 47 });
			return;
		case ' ':
			doType(new int[] { 32 });
			return;
		}
		throw new IllegalArgumentException("Cannot type character " + character);
	}

	protected void doType(int... keyCodes) {
		doType(keyCodes, 0, keyCodes.length);
	}

	protected void doType(int[] keyCodes, int offset, int length) {
		if (length == 0)
			return;
		this.robot.keyPress(keyCodes[offset]);
		doType(keyCodes, offset + 1, length - 1);
		this.robot.keyRelease(keyCodes[offset]);
	}
}
