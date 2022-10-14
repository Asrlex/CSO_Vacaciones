package gui.admin;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import varios.acciones.MoveMouseListener;
import varios.components.SortedComboBoxModel;
import varios.components.SortedListModel;
import varios.dao.DAO;
import varios.objetos.Empleado;


public class DiasPasados implements Runnable{
	
	private GridBagConstraints c = new GridBagConstraints();
	private DAO dao = DAO.getInstance();
	private Font font = new Font("Bankia", Font.BOLD, 14);
	
	public void run(){
		
		Pasado pasado = new Pasado();
		//---PROPIEDADES GUI---
		GridBagLayout gbl = new GridBagLayout();
		pasado.setLayout(gbl);
		c.insets = new Insets(10, 10, 10, 10);
		pasado.getContentPane().setBackground(Color.WHITE);
		pasado.setUndecorated(true);
		pasado.getRootPane().setBorder(BorderFactory.createMatteBorder(4, 4, 4, 4, Color.ORANGE));
		
		//---Barra de tareas---
		pasado.addMenuBar();
		
		//---Calendario---
		pasado.addBody();
		
		pasado.pack();
		pasado.setVisible(true);
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		pasado.setLocation(dim.width/2-pasado.getSize().width/2, dim.height/2-pasado.getSize().height/2);
	}
	
	class Pasado extends JFrame{

		private static final long serialVersionUID = 1L;
		
		private void addMenuBar(){
			JMenuBar bar = new JMenuBar();
				bar.setLayout(new GridBagLayout());
				bar.setBackground(Color.WHITE);
			
			JButton exit = new JButton(dao.getSalir());
				exit.setContentAreaFilled(false);
				exit.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
			
			JButton min = new JButton(dao.getMinimizar());
				min.setContentAreaFilled(false);
				min.setBorder(BorderFactory.createEmptyBorder(0, 400, 0, 50));
				
				MoveMouseListener.generar(bar);
				bar.add(Box.createHorizontalGlue());
				bar.add(min);
				bar.add(exit);
				setJMenuBar(bar);
				
			min.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					setState(Frame.ICONIFIED);
				}
			});
			exit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					dispose();
				}
			});
		}
		
		private void addBody(){
			
			JPanel panel = new JPanel();
				panel.setBackground(Color.WHITE);
				panel.setLayout(new GridBagLayout());
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 15;
			c.gridwidth = 20;
			add(panel, c);
			
			JPanel top = new JPanel();
				top.setBackground(Color.white);
				top.setLayout(new GridLayout(0, 1, 5, 5));
			JLabel usuarios = new JLabel("Seleccione el usuario: ");
				usuarios.setBackground(Color.white);
				usuarios.setFont(font);
				top.add(usuarios);
			c.gridx = 0;
			c.gridy = 0;
			c.gridheight = 5;
			c.gridwidth = 20;
			panel.add(top, c);
				
			SortedComboBoxModel<String> modelo = new SortedComboBoxModel<String>();
			modelo.addElement("-----");
			for(Empleado e : dao.getListaEquipo())
				modelo.addElement(e.getNombre() + " - " + e.getUsuario());
			JComboBox<String> usuariosCB = new JComboBox<String>(modelo);
				usuariosCB.setBackground(Color.white);
				usuariosCB.setFont(font);
				top.add(usuariosCB);
			
			SortedListModel modelo_lista = new SortedListModel();
			JList<String> lista = new JList<String>();
				lista.setModel(modelo_lista);
				lista.setBackground(Color.white);
				lista.setFont(font);
				DefaultListCellRenderer renderer =  (DefaultListCellRenderer)lista.getCellRenderer();
				renderer.setHorizontalAlignment(JLabel.CENTER);
			JScrollPane sp = new JScrollPane(lista);
				sp.setBackground(Color.WHITE);
				sp.setPreferredSize(new Dimension(150, 200));
			c.gridx = 0;
			c.gridy = 5;
			c.gridheight = 10;
			c.gridwidth = 15;
			panel.add(sp, c);
			
			JButton borrar = new JButton("Borrar");
				borrar.setBackground(Color.WHITE);
				borrar.setFont(font);
			c.gridx = 15;
			c.gridy = 5;
			c.gridheight = 10;
			c.gridwidth = 5;
			panel.add(borrar, c);
			
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyy");
			usuariosCB.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String selec = (String) usuariosCB.getSelectedItem();
					modelo_lista.removeAllElements();
					if(!selec.equals("-----")){
						for(LocalDate d : dao.getApproved(
								selec.substring(selec.length()-7, selec.length()), 0))
							modelo_lista.addElement(dtf.format(d));
					}
					modelo_lista.sort(SortedListModel.DATES);
				}
			});
			
			borrar.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					String dia = lista.getSelectedValue();
					String usuario = (String) usuariosCB.getSelectedItem();
					if(dia != null)
						if(JOptionPane.showConfirmDialog(null, 
								"¿Desea borrar el día " + dia + " de " + usuario + "?", 
								"Confirmación", JOptionPane.YES_NO_OPTION) == 0){
							dao.borrarDiaPasado(dia, 
									usuario.substring(usuario.length()-7, usuario.length()));
							usuariosCB.setSelectedItem("-----");
						}
					else
						JOptionPane.showMessageDialog(null, "Seleccione un día.", 
							"Aviso", JOptionPane.YES_NO_OPTION);
				}
			});
		}
	}
}
