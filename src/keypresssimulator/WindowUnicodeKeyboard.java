package keypresssimulator;

import java.awt.Robot;

public class WindowUnicodeKeyboard extends Keyboard {
	private Robot robot;

	public WindowUnicodeKeyboard(Robot robot) {
		super(robot);
		this.robot = robot;
	}

	public void type(char character) {
		try {
			super.type(character);
		} catch (IllegalArgumentException e) {
			String unicodeDigits = String.valueOf(Character.codePointAt(String.valueOf(character), 0));
			this.robot.keyPress(18);
			for (int i = 0; i < unicodeDigits.length(); i++)
				typeNumPad(Integer.parseInt(unicodeDigits.substring(i, i + 1)));
			this.robot.keyRelease(18);
		}
	}

	private void typeNumPad(int digit) {
		switch (digit) {
		case 0:
			doType(new int[] { 96 });
			break;
		case 1:
			doType(new int[] { 97 });
			break;
		case 2:
			doType(new int[] { 98 });
			break;
		case 3:
			doType(new int[] { 99 });
			break;
		case 4:
			doType(new int[] { 100 });
			break;
		case 5:
			doType(new int[] { 101 });
			break;
		case 6:
			doType(new int[] { 102 });
			break;
		case 7:
			doType(new int[] { 103 });
			break;
		case 8:
			doType(new int[] { 104 });
			break;
		case 9:
			doType(new int[] { 105 });
			break;
		}
	}
}
