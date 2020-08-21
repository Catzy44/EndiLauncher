package me.catzy44.ui;

import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class JMultilineLabel extends JTextPane{
    private static final long serialVersionUID = 1L;
    public JMultilineLabel(String text){
        //super(text);
        setEditable(false);  
        setCursor(null);  
        setOpaque(false);  
        setFocusable(false);  
        setFont(UIManager.getFont("Label.font"));      
        //setWrapStyleWord(true);  
        //setLineWrap(true);
        //According to Mariana this might improve it
        setBorder(new EmptyBorder(5, 5, 5, 5));  
        setAlignmentY(JLabel.CENTER_ALIGNMENT);
        
        putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        setContentType("text/html");//
        //setText("<html><style>html {white-space:nowrap}</style>"+text.replace("\r\n", "<br>").replace("\n", "<br>")+"</html>");
        setText("<html style=\"white-space:nowrap;text-align:center\">"+text.replace("\r\n", "<br>").replace("\n", "<br>")+"</html>");
    }
}