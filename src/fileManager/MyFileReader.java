/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fileManager;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


/**
 *
 * @author aftab
 */
public class MyFileReader {
   
    FileInputStream fis;
    
    public MyFileReader(File file,long startByte,long endByte) throws FileNotFoundException, IOException
    {
        fis = new FileInputStream(file);
       
        for (int i = 0; i < startByte; i++) {
            fis.read();
        }
        for (int i = 0; i < endByte-startByte; i++) {
            fis.read();
            
        }
            
        
        
       
    }
    
}
