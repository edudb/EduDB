/*
EduDB is made available under the OSI-approved MIT license.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package net.edudb.user_interface;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import adipe.translate.TranslationException;
import net.edudb.console.DatabaseConsole;
import net.edudb.engine.DatabaseSystem;

public class Main {

	public static void main(String[] args) throws TranslationException, AWTException {

		/**
		 * ATTENTION
		 * 
		 * Important call.
		 */
		DatabaseSystem.getInstance().initializeDirectories();

		//
//		final TrayIcon trayIcon;
//
//		if (SystemTray.isSupported()) {
//
//			SystemTray tray = SystemTray.getSystemTray();
//			Image image = Toolkit.getDefaultToolkit().getImage("./E.png");
//
//			ActionListener exitListener = new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					DatabaseSystem.getInstance().exit(0);
//				}
//			};
//
//			PopupMenu popup = new PopupMenu();
//			MenuItem defaultItem = new MenuItem("Quit Server");
//			defaultItem.addActionListener(exitListener);
//			popup.add(defaultItem);
//
//			trayIcon = new TrayIcon(image, "EduDB", popup);
//
//			trayIcon.setImageAutoSize(true);
//
//			try {
//				tray.add(trayIcon);
//			} catch (AWTException e) {
//				System.err.println("TrayIcon could not be added.");
//			}
//
//		} else {
//		}
		//

		 DatabaseConsole console = DatabaseConsole.getInstance();
		 console.setPrompt("edudb$ ");
		 console.start();
	}

}