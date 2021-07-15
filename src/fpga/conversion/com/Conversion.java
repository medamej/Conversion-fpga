
package fpga.conversion.com;

import javax.swing.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Conversion extends JFrame implements ActionListener {
    
	public File file=null;
	String choosertitle;
    JFileChooser fc;
    JButton b, b1, b2;
    JLabel l1 ,x;
    JTextField tf, p1;
    FileInputStream in;
    Socket s;
    DataOutputStream dout;
    DataInputStream din;
    int i;

    Conversion() {
    	
    	JFrame frame = new JFrame("dddddd");
    	p1=new JTextField("fff");
    	p1.setBounds(60,100,320,30);
    	p1.setText("");
    	add(p1);
    	JLabel label = new JLabel("Your text here");
    	x=new JLabel("");
    	x.setBounds(0,100,200,30);
    	x.setText("File Path:");
    	add(x);

        b = new JButton("Télécharger fichier .vhd");
        b.setBounds(100,50,200,30);
        add(b);
        b.addActionListener(this);
        
        b1 = new JButton("Convertir");
        b1.setBounds(100,200,200,30);
        add(b1);
        b1.addActionListener(this);
        
        b2 = new JButton("choisir l'emplacement");
        b2.setBounds(100,150,200,30);
        add(b2);
        b2.addActionListener(this);
        
    	UIManager.put("FileChooser.readOnly", Boolean.TRUE);
        fc = new JFileChooser();
        
        setLayout(null);
        setSize(400, 300);
        setVisible(true);
        try {
            s = new Socket("localhost", 10);
            dout = new DataOutputStream(s.getOutputStream());
            din = new DataInputStream(s.getInputStream());
            send();
        } catch (Exception e) {
        }
    }

	public static String getEquivalent(String step) {
		System.out.println("steeeeeps:" + step);
		try {
			File file1 = new File("FPGA_Dict.xlsx");
			// obtaining bytes from the file
			FileInputStream fis = new FileInputStream(file1);
			// creating Workbook instance that refers to .xlsx file
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			// creating a Sheet object to retrieve object
			XSSFSheet sheet = wb.getSheetAt(0);
			// iterating over excel file
			Iterator<Row> itr = sheet.iterator();

			String str3 = "";


			///////// La partie De La Conversion ///////////////

			while (itr.hasNext()) {
				Row row = itr.next();
				// iterating over each column
				Iterator<Cell> cellIterator = row.cellIterator();
				while (cellIterator.hasNext()) {
					Cell cell = cellIterator.next();
					// power_on_reset
					Pattern pattern0 = Pattern.compile("power(.*?)reset");
					Matcher matcher0 = pattern0.matcher(step);

					if (matcher0.find()) {
						return "Power on the reset";
					}
					// activate_reset
					Pattern pattern1 = Pattern.compile("activate_reset(.*?)");
					Matcher matcher1 = pattern1.matcher(step);

					if (matcher1.find()) {
						return "Activate the reset";
					}

					// deactivate_reset
					Pattern pattern2 = Pattern.compile("deactivate_reset(.*?)");
					Matcher matcher2 = pattern2.matcher(step);

					if (matcher2.find()) {
						return "Deactivate the reset";
					}
					
					//SET with V_
				    Pattern pattt = Pattern.compile("SET_(.*?)_COND.*[(](.*?),");
				    Matcher matchrrr = pattt.matcher(step);
				    Pattern pattern3 = Pattern.compile("_(.*?)_F");
					Matcher matcher3 = pattern3.matcher(step);
					String motfound3 = "";
					Pattern pattern4 = Pattern.compile("_(.*?)_T");
					Matcher matcher4 = pattern4.matcher(step);
					String motfound4 = "";
				    if(matchrrr.find()) {
				        return "set " + matchrrr.group(1) + " to "+ matchrrr.group(2);
				    }
					
					// SET_SIGNAL_NAME_FALSE"
					
				    else if (matcher3.find()) {
						motfound3 = matcher3.group(1);
						return "SET " + motfound3 + " TO FALSE";
					}

					// SET_SIGNAL_NAME_TRUE"
					
					
				    else if (matcher4.find()) {
						motfound4 = matcher4.group(1);
						return "SET " + motfound4 + " TO TRUE";
					}
				    
					// SET_PDP_LOCATION_PDP(1&2)
					Pattern pattern5 = Pattern.compile("SET_PDP_LOCATION[(](.*?),");
					Matcher matcher5 = pattern5.matcher(step);
					String motfound5 = "";

					if (matcher5.find()) {
						motfound5 = matcher5.group(1);
						return "SET the PDP LOCATION to " + motfound5;
					}
	

					// check_output
					Pattern pattern6 = Pattern.compile("check_output.*spy.(.*?),.*'(.*?)', s_count");
					Matcher matcher6 = pattern6.matcher(step);
					String motfound6 = "";
					// check_output with DURING
					Pattern pattern7 = Pattern.compile("check_output.*spy.(.*?),.*'(.*?)',(.*?),(.*?),");
					Matcher matcher7 = pattern7.matcher(step);
					String motfound7 = "";

					if (matcher6.find()) {
						motfound6 = matcher6.group(1);
						return "Verify by spy " + motfound6 + " is equal to " + matcher6.group(2);
					}

					else if (matcher7.find()) {
						motfound7 = matcher7.group(1);
						return "Verify by spy " + motfound7 + " is equal to " + matcher7.group(2) + ""
								+ matcher7.group(3) + "" + matcher7.group(4);
					}
					   //************ if else of the new check***************

				    Pattern p = Pattern.compile("check.*spy.(.*?),.*v_(.*?),");
				    Matcher m = p.matcher(step.trim());
				    Pattern pa = Pattern.compile("check.*spy.(.*?),.*v_(.*?),.*WITHIN,(.*?),");
				    Matcher ma = pa.matcher(step.trim());
				    
				    //check with WITHIN

				    if(ma.find()) {
				        return "verify by spy " +" "+ma.group(1) +" is equal to"+" "+"v_"+ma.group(2)+" "+ "WITHIN" + ma.group(3);
				    }
				    //another check
				    else if(m.find()) {
				        return "verify by spy " +" "+m.group(1) +" is equal to"+" "+"v_"+m.group(2);
				    }
					// check_input
					Pattern pattern8 = Pattern.compile("check_input.*spy.(.*?),.*'(.*?)',");
					Matcher matcher8 = pattern8.matcher(step);
					String motfound8 = "";

					if (matcher8.find()) {
						motfound8 = matcher8.group(1);
						return "Verify by spy " + motfound8 + " is equal to " + matcher8.group(2);
					}

					// check_arinc
					Pattern pattern9 = Pattern.compile("check_arinc.*[(](.*?),.*'(.*?)'");
					Matcher matcher9 = pattern9.matcher(step);
					String motfound9 = "";

					if (matcher9.find()) {
						motfound9 = matcher9.group(1);
						return "Verify through ARINC link that " + motfound9 + " is equal to " + matcher9.group(2);
					}
					// check_timing
					Pattern pattern10 = Pattern.compile("check_timing.*spy.(.*?),.*'(.*?)',(.*?),");
					Matcher matcher10 = pattern10.matcher(step);
					String motfound10 = "";

					if (matcher10.find()) {
						motfound10 = matcher10.group(1);
						return "Verify by spy " + motfound10 + " is equal to " + matcher10.group(2) + " in less than "
								+ matcher10.group(3).trim();
					}

					// extract v_val***
					Pattern pattern11 = Pattern.compile("(.*?):.*=(.*?);");
					Matcher matcher11 = pattern11.matcher(step);
					String motfound11 = "";
					if (matcher11.find()) {
						motfound11 = matcher11.group(1);
						return motfound11 + " is equal to " + matcher11.group(2).trim();
					}

                    //check_adc
					Pattern pattern14 = Pattern.compile("check.*spy.(.*?),.* (.*?),");
					Matcher matcher14 = pattern14.matcher(step);
					String motfound14 = "";
					if (matcher14.find()) {
						motfound14 = matcher14.group(1);
						return "Verify by spy " + motfound14 + " is equal to " + matcher14.group(2);
					}
					//set adc
					Pattern pattern15 = Pattern.compile("c_(.*?), .*v_adc(.*?),");
				    Matcher matcher15 = pattern15.matcher(step);
					String motfound15 = "";
				    if(matcher15.find()) {
						motfound15 = matcher15.group(1);
				        return "set by adc" +" "+motfound15 +" is equal to"+" "+"v_adc"+matcher15.group(2);
				    }

				}

			}

			wb.close();

			System.out.println("");
			System.out.println();

		} catch (Exception e) {
			e.printStackTrace();

		}
		return " ";
	}
    
    public void actionPerformed(ActionEvent e) {
        try {
            if (e.getSource() == b) {
				//JFileChooser jFileChooser = new JFileChooser();
				int x = fc.showSaveDialog(null);

                if (x == JFileChooser.APPROVE_OPTION) {
                	file = fc.getSelectedFile();
                	String l = fc.getSelectedFile().getAbsolutePath();
                	p1.setText(l);

					FileOutputStream fileOutputStream;
					try {
						fileOutputStream = new FileOutputStream(file);
						fileOutputStream.close();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(null, e1.getMessage());
					}
                }
                
            }
            if(e.getSource() == b2) {
                fc.setCurrentDirectory(new java.io.File("."));
                fc.setDialogTitle(choosertitle);
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                fc.setAcceptAllFileFilterUsed(false);
                //    
                if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) { 
                  System.out.println("CurrentDirectory(): " +  fc.getCurrentDirectory());
                  System.out.println("SelectedFile() : " +  fc.getSelectedFile());
                  }
                else {
                  System.out.println("No Selection ");
                  }
            	
            }
            if (e.getSource() == b1) {
            	try {
					//File myObj = new File(file);
					//FileWriter myWriter = new FileWriter(fc.getSelectedFile()+"/output.txt");
					// Blank Document
					XWPFDocument document = new XWPFDocument();
					// Write the Document in file system
					
					// créer un paragraphe
					XWPFParagraph paragraph = document.createParagraph();
					// créer l'objet run
					XWPFRun run = paragraph.createRun();
					XWPFRun run2 = paragraph.createRun();

					String str = "";
					String str2 = "";
					System.out.println("FILE : "+file);
					Scanner myReader = new Scanner(file);
					String MOT0="";
					String MOT00="";
					String MOT000="";
					String MOT0000="";
					String MOT = "";
					String MOT2 = "";
					String MOT3 = "";
					String MOT4 = "";
					String MOT5 = "";
					String MOT6 = "";
					String MOT7 = "";
					String WORD = "";
					String WORD1 = "";
					String WORD2 = "";
					String title = "";
					String version = "";
					int i = 1;
					int j = 1;
					int k = 1;

					ArrayList<String> objectives = new ArrayList<String>();
					ArrayList<String> requiremnt = new ArrayList<String>();
					ArrayList<ArrayList<String>> steps = new ArrayList<ArrayList<String>>();
					boolean Vfound = false;
					while (myReader.hasNextLine()) {
						String data = myReader.nextLine().trim();
						data = data.trim();

						////// Extract Title Name //////
						if (data.contains("TITLE")) {
							String[] tokens = data.split(" ");
							title = tokens[12];
							str += title + "/" + "	The objectives below verify requirement ";
							System.out.println(title + "/:");
						}
						//////// Extract Version //////
						if (data.contains(" VERSION") && !Vfound) {
							String[] tokens = data.split(" ");
							version = tokens[8];
							str += title + "/" + version + "		The objectives below verify requirement ";
							run.setText(title + "/" + version + "	  ");
							run.addBreak();
							System.out.println(title + "/" + version);
							Vfound = true;
						}
						//////// Extract Signal Name //////
						
						if (data.contains("The following procedure verifies the condition ")) {
							String[] tokens = data.split(" ");
							MOT = tokens[7];
							System.out.println("The following procedure verifies the condition " + " " + MOT);
							run.setText("\r\nThe following procedure verifies the condition" + " " + MOT);

						}else if (data.contains("The following procedure verifies the condition")) {
							String[] tokens = data.split(" ");
							MOT0 = tokens[7];
							MOT00= tokens[8];
							MOT000= tokens[9];
							System.out.println("The following procedure verifies the condition " + " " + MOT0 + "and"+ MOT00);
							run.setText("\r\nThe following procedure verifies the condition " + " " + MOT0 +" " + MOT00+ " "+ MOT000);
						} else if (data.contains("The following procedure verifies the ADC signal ")) {
							String[] tokens = data.split(" ");
							MOT2 = tokens[8];
							MOT3 = tokens[9];
							MOT4 = tokens[10];
							System.out.println(
									"The following procedure verifies the ADC signal" + " " + MOT2 + " " + MOT3 + " " + MOT4);
							run.setText("\r\nThe following procedure verifies the ADC signal" + " " + MOT2 + " " + MOT3 + " "
									+ MOT4);
						}
						else if (data.contains("The following procedure verifies the signal ")) {
							String[] tokens = data.split(" ");
							MOT5 = tokens[7];
							System.out.println(
									"The following procedure verifies the signal" + " " + MOT5);
							run.setText("\r\nThe following procedure verifies the signal" + " " + MOT5);
						}
						//////// Extract Requirement //////
						
						if (data.contains("to the following requirements")) {
							String[] tokens = data.split(" ");
							WORD = tokens[1];
							WORD1 = tokens[2];
							data = myReader.nextLine().trim();

							System.out.print("The objectives below verify requirement ");
							while (!data.contains("#######")) {
								System.out.println("--------------- " + data.substring(7, data.length()) + ",");
								requiremnt.add(data.substring(7, data.length()));
								data = myReader.nextLine().trim();
							}
							run.setText(" "+"according to the following requirements:");
							for (String string : requiremnt) {
								str += string + ", ";
								run.setText(string + ",");
							}

						}
						///// Extract objectives///////
						if (data.contains("Objective ")) {
							data = myReader.nextLine().trim();
							run.addBreak();
							run.addBreak();
							str += "\nObjective " + (i) + ":\n";
							run2.setBold(true);
							run.setText("\n******************** Objective " + (i) + ":********************\n");
							run.addBreak();

							i++;
							while (!data.contains("******")) {
								System.out.println("+++++++++++++++++ " + data.substring(3, data.length()));
								objectives.add(data);
								str += data + ".";
								run.setText(data.substring(3, data.length()) + ".");
								run.addBreak();

								data = myReader.nextLine().trim();

							}
							run.addBreak();
							Scanner newreader = myReader;
							String newdata = myReader.nextLine().trim();
							ArrayList<String> step = new ArrayList<String>();

							newdata = newreader.nextLine().trim();

							while (newreader.hasNextLine()) {
								newdata = newdata.trim();
								if (newdata.contains("Objective ")) {
									newdata = myReader.nextLine().trim();

									str += "\nObjective " + (i) + ":\n";
									run.setText("\n******************** Objective " + (i) + ":********************\n");
									run.addBreak();
									str2 += "\nObjective " + ":\n";
									i++;

									while (!newdata.contains("******")) {
										System.out.println("+++++++++++++++++ " + newdata.substring(3, newdata.length()));
										objectives.add(newdata);
										str += newdata + ".";
										run.setText(newdata.substring(3, newdata.length()) + ".");
										run.addBreak();

										newdata = myReader.nextLine().trim();
									}

								}
								//////// Extract Steps //////////
								if (newdata.contains("STEP ")) {
									newdata = newreader.nextLine().trim();
									newdata = newreader.nextLine().trim();
									newdata = newreader.nextLine().trim();

									str2 += "\nStep " + (j) + ":\n\n";
									run.addBreak();
									run.setText("\t");
									run.setText("######## Step  " + (j) + " :######## ");
									j++;
									while (!newdata.isEmpty() && !newdata.contains("Final")) {
										// System.out.println("XXXXXX " + newdata.substring(4, newdata.length()));
										String value = getEquivalent(newdata);
										System.out.println("VALUE:*** " + value);
										step.add(newdata);
										run.addBreak();
										str2 += value + "\n";
										run.setText("\t");
										run.setText((k) + "-   " + value + ".");
										k++;
										run.addBreak();
										newdata = newreader.nextLine().trim();
									}
									newdata = newreader.nextLine().trim();
									steps.add(step);
									System.out.println(newdata);
									str2 += newdata;
									run.setText(newdata);
									run.addBreak();
								}
								newdata = newreader.nextLine().trim();
								System.out.println(newdata);
							}
							str += "\n\n";
							run.addBreak();

						}
					}
					FileOutputStream out = new FileOutputStream(new File(fc.getSelectedFile()+"/"+title+".docx"));
					document.write(out);
					document.close();
					out.close();

					myReader.close();
					//myWriter.write(str);
					//myWriter.write(str2);
					//myWriter.close();
				} catch (FileNotFoundException e1) {
					System.out.println("An error occurred.");
					e1.printStackTrace();
				}
                send();
            }
        } catch (Exception ex) {
        }
    }

    public void copy() throws IOException {
        File f1 = fc.getSelectedFile();
        tf.setText(f1.getAbsolutePath());
        in = new FileInputStream(f1.getAbsolutePath());
        while ((i = in.read()) != -1) {
            System.out.print(i);
        }
    }

    public void send() throws IOException {
        dout.write(i);
        dout.flush();

    }

    public static void main(String... d) {
        new Conversion();
    }
}
