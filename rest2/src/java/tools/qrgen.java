/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;

/**
 *
 * @author nilay jha
 */
public class qrgen {
    public static ByteArrayOutputStream getQRc(String qrtext){
        
return QRCode.from(qrtext).to(ImageType.PNG).withSize(100, 100).stream();

    }
    public static File getQrcf(String qrtext) throws IOException{
        File f1=QRCode.from(qrtext).to(ImageType.PNG).withSize(100, 100).file();
        BufferedImage image = ImageIO.read(f1);
     BufferedImage newImage = new BufferedImage(image.getWidth(), 
            image.getHeight() + 20, BufferedImage.TYPE_INT_ARGB);
      Graphics g2 = newImage.getGraphics();
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        g2.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
        g2.setColor(Color.BLACK);
        g2.setFont(g2.getFont().deriveFont(11f));
        g2.drawString(qrtext,0,image.getHeight()+5);
        g2.dispose();
    ImageIO.write(newImage, "png", f1);
    return f1;
    }
}
