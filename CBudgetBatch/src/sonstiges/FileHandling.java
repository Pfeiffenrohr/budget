package sonstiges;

/*            Alles was mit Files Lesen + Schreiben zu tun hat           */
/* Author : Richard Lechner */
/* company : Cenit AG Systemhaus */
/* Datum : 22.05.2003 */
/** ********************************************************************** */
import java.io.*;
import java.util.*;
/**
 * Enthält Methoden zum Lesen und Schreiben der Dateien. Außerdem sind noch
 * einige "Spezialmethoden" drin
 *
 * @author Richard Lechner
 * @version 1.1
 */
public class FileHandling {
    /***************************************************************************
     * Schaut nach, ob das file "str" existiert
     *
     * @param str
     *            Pfad der Datei
     * @return "false" falls Datei nicht existiert/ "true" wenn Datei existie
     **************************************************************************/
    public boolean file_exists(String str) {
        try
        {
            File datei = new File(str);
            if (datei.exists()) {
                return true;
            }
            return false;
        }catch (Exception exc)
        {
            //new ErrorFrame("Internal Error! Trying to check a null-directory",false);
            return false;
        }
    }
    /***************************************************************************
     * Schaut nach, ob das Verzeichnis existiert, in dem die Datei "str" liegt
     *
     * @param str
     *            Pfad der Datei
     * @param bcreate Verzeichnis erstellen, falls dieses nicht existiert: true=erstellen
     * @return "false" falls VZ nicht existiert/ "true" wenn VZ existiert
     **************************************************************************/
    public boolean directory_exists(String str, boolean bcreate) {
        try{

            File datei = new File(str);
            String tempdatei = datei.getParent();
            File filetempdatei = new File(tempdatei);
            if (filetempdatei.isDirectory()) {return true;}

            if (bcreate)
            {
                if (filetempdatei.mkdirs()) {return true;}
                return false;
            }
            return false;

        }catch (Exception exc){
            //new ErrorFrame("Internal Error!! Trying to check a null directory",true);
            return false;
        }
    }
    /**
     * schaut nach, ob eine Datei leer ist
     * @param filename
     * @return
     */
    public boolean isNotEmpty(String filename)
    {
        File datei = new File(filename);
        if (datei.length()>0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    /***************************************************************************
     * Testet, ob eine Datei schreibbar ist
     * @param str Dateiname
     * @return true, wenn Datei schreibbar ist
     ***************************************************************************/
    public boolean isWritable(String str)
    {
        File datei = new File(str);
        if (! datei.canWrite())
        {
            return false;
        }

        return true;

    }
    /***************************************************************************
     * Testet, ob ein Verzeichnis existiert
     * @param str Verzeichnisname
     * @return true, falls Name Parameter ein Verzeichnis ist
     **************************************************************************/
    public boolean isDirectory(String str)
    {
        File datei = new File(str);
        if (! datei.isDirectory())
        {
            return false;
        }

        return true;

    }

    /***************************************************************************
     * Liefert eine Liste der Filenamen, die ein Directory enthält.
     * @param str Verzeichnisname
     * @return String[], falls directory exisitiert und gelesen werden kann
     *         null, falls das Directory nicht existiert
     **************************************************************************/
    public String[] listFileNames(String str)
    {
        String [] resArray={""};

        try
        {
            File[] dirContent = new File(str).listFiles();
            if (dirContent==null)
            {
                return resArray;
            }
            resArray = new String[dirContent.length];
            for (int count=0;count < dirContent.length; count++)
            {
                resArray[count] = dirContent[count].getName();
            }
            return resArray;
        }
        catch (SecurityException se )
        {
            System.err.println ("Can not read "+ str);
            return null;
        }
    }

    /**
     * Legt ein Verzeichnis mit dem angegebenen absoluten Pfad an
     * @param str Absoluter Pfad der Datei
     * @return anlegen des Verzeichnisses ergolgreich Ja/Nein
     */
    public boolean makeDir(String str)
    {
        File datei = new File(str);
        if (! file_exists(str))
        {
            if (! datei.mkdirs())
            {
                System.err.println("!! Can not create "+str);
                return false;
            }
        }
        return true;
    }
    /***************************************************************************
     * liest File, gibt den Inhalt als String zurück
     * behält dabei Zeilenumbrüche wie in Datei (Unix/Windows)
     *
     * @param filename
     *            Pfad der Datei
     * @return Inhalt der Datei
     **************************************************************************/
    public String readFile(String filename) {
        final int BUFSIZE = 128;    // FIXME: bessere Größe?
        StringBuffer stb = new StringBuffer("");
        BufferedReader br = null;
        FileReader fr = null;
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
            boolean eof = false;
            char[] buf = new char[BUFSIZE];
            while(!eof) {
                int nret = br.read(buf, 0, buf.length);
                if( -1 == nret)
                    eof = true;
                else
                    stb.append(buf, 0, nret);
            }
        } catch (IOException e) {
            System.err.println("****Error! Can not read file" + filename
                    + "****");
            return "";
        } catch(Exception e) {
            /* FIXME: empty */
        }  finally {
            try {
                br.close();
                fr.close();
            } catch(IOException e) {
                System.err.println("****Error! Can not close file descriptor for " + filename
                        + "****");
            } catch(NullPointerException npe) {}
        }
        return stb.toString();
    }

    /***************************************************************************
     * liest File, gibt den Inhalt als Vector zurück
     *
     * @param filename
     *            Pfad der Datei
     * @return Inhalt der Datei
     **************************************************************************/
    public Vector readFileAsVector(String filename) {
        String thisLine;
        Vector inputs = new Vector();
        try {
            InputStream is = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((thisLine = br.readLine()) != null) {
                inputs.addElement(thisLine);
            }
            is.close();
        } catch (Exception e) {
            //new ErrorFrame(
            //e.toString(),
            //	true);
        }
        return inputs;
    }

    /***************************************************************************
     * liest File, gibt den Inhalt als Hashtable zurück
     * Die Datei sollte folgendes Format befolgen:
     *
     * key value
     *
     * value kann auch Leerzeichen enthalten.
     *
     * @param filename
     *            Pfad der Datei
     * @return Inhalt der Datei
     **************************************************************************/
    //TODO Methode hat Probleme, wenn im Wert ein Leerzeichen vorkommt
    public Hashtable readFileAsHashtable(String filename) {
        String thisLine;
        int pos_spc;
        int pos_tab;
        int pos;
        Hashtable inputs = new Hashtable();
        try {
            InputStream is = new FileInputStream(filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String br_line;
            //	DebugFrame.Msg(8,"Start reading from buffer");
            while ((br_line = br.readLine()) != null) {
                thisLine = br_line.trim();
                if (thisLine.startsWith("#") || thisLine.startsWith(";")) {
                    //	DebugFrame.Msg(7,"Ignore comment line: "+thisLine);
                    continue;
                }
                //DebugFrame.Msg(8,"line: "+thisLine);
                pos_spc = thisLine.indexOf(' ');
                pos_tab = thisLine.indexOf('\t');
                //DebugFrame.Msg(8,"    pos_spc = "+pos_spc);
                //DebugFrame.Msg(8,"    pos_tab = "+pos_tab);

                if (pos_spc == -1 && pos_tab == -1) {
                    // Kein VALUE vorhanden, nur KEY
                    if (thisLine.length() > 0) {
                        String tmp = new String().trim();
                        inputs.put(thisLine.trim(),"");
                    }
                } else {
                    /*
                     * Folgende Fälle werden betrachtet:
                     * 1. KEY [SPACE] VALUE					-> pos_tab == -1
                     * 2. KEY [TAB] VALUE					-> pos_spc == -1
                     *    (hier kann man noch erwähnen, dass wenn kein VALUE da ist,
                     *     also beide Variablen -1 sind, bereits ob abgefangen wurde)
                     * 3. KEY [SPACE] VALUE [SPACE] VALUE	-> bereits in 1. enthalten
                     * 4. KEY [SPACE] VALUE [TAB] VALUE		-> pos_spc < pos_tab
                     * 4. KEY [TAB] VALUE [SPACE] VALUE		-> pos_spc > pos_tab
                     * 4. KEY [TAB] VALUE [TAB] VALUE		-> bereits in 2. enthalten
                     */
                    if (pos_tab == -1) {
                        pos = pos_spc;
                    } else if (pos_spc == -1) {
                        pos = pos_tab;
                    } else {
                        if (pos_spc < pos_tab) {
                            pos = pos_spc;
                        } else {
                            pos = pos_tab;
                        }
                    }
                    String key = thisLine.substring(0,pos);
                    //DebugFrame.Msg(7,"Add to hash("+key+") = "+thisLine.substring(pos+1).trim());
                    inputs.put(key,thisLine.substring(pos+1).trim());
                }
            }
        } catch (Exception e) {
            //new ErrorFrame(
            //		e.toString(),
            //		true);
        }
        return inputs;
    }

    /***************************************************************************
     * Schreibt ein Inifile (allgemein) in der Form: <br>
     * key value <br>
     * key value <br>
     * usw... Als Trennzeichen wird immer das Leerzeichen verwendet
     *
     * @param ini
     *            Hashtable mit Schlüsseln, die geschrieben werden sollen
     * @param inifile
     *            Pfad und Name des Files
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetreten ist
     **************************************************************************/
    public boolean writeIniFile(Hashtable ini, String inifile) {
        String key;
        String value;
        Enumeration keys = ini.keys();
        try {
            FileOutputStream os = new FileOutputStream(inifile);
            while (keys.hasMoreElements()) {
                key = (String) keys.nextElement();
                value = (String) ini.get(key);
                for (int i = 0; i < key.length(); i++) {
                    os.write(key.charAt(i));
                }
                os.write(' ');
                for (int i = 0; i < value.length(); i++) {
                    os.write(value.charAt(i));
                }
                os.write('\n');
            }
            os.close();
        } catch (IOException ioe) {
            System.err.println("****Error! Can not write INIfile****"+ioe);
            return false;
        }
        return true;
    }
    /***************************************************************************
     * Liest ein IniFile und schreibt die Daten in einen Hash Das Inifile hat
     * die Form: <br>
     * key value <br>
     * key value <br>
     * usw
     *
     * @param hash
     *            Hashtabelle mit allen Schlüsseln (werden selber angelegt)
     * @param inifile
     *            Pfad und Name des Inifiles
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetreten ist
     **************************************************************************/
    public boolean readIniFile(Hashtable hash, String inifile) {
        FileInputStream is;
        StringBuffer stb = new StringBuffer();
        String param = "";
        if (!file_exists(inifile)) {
            System.err.println("Can not read " + inifile + " !!!!");
            return false;
        }
        try {
            is = new FileInputStream(inifile);
            int ch;
            ch = is.read();
            while (-1 != ch) {
                //while(count<2&&ch!=-1)
                stb.setLength(0);
                while (((char) ch != ' ') && ((char) ch != '\n') && (ch != -1)) {
                    stb.append((char) ch);
                    ch = is.read();
                };
                ch = is.read();
                param = stb.toString();
                stb.setLength(0);
                while (((char) ch != '\n') && (ch != -1)) {
                    stb.append((char) ch);
                    ch = is.read();
                }
                ch = is.read();
                hash.put(param, stb.toString());
                if ((char) ch == '\n')
                    ch = is.read();
            }
            is.close();
        } catch (IOException ioe) {
            System.err.println("Fehler beim Lesen desINI-Files");
            return false;
        }
        /*
         * Enumeration e = hash.elements(); while (e.hasMoreElements()) {
         * System.out.println((String)e.nextElement());
         */
        return true;
    }
    /***************************************************************************
     * Liest ein IniFile und schreibt die Daten in einen Hash Das Inifile hat
     * die Form: <br>
     * key value <br>
     * key value <br>
     * usw Als Trennzeichen kann jedes beliebige Zeichen mitgegeben werden.
     * Kommentar mit '#' und '!' wird übersprungen und nicht eingelesen
     *
     * @param hash
     *            Hashtabelle mit allen Schlüsseln (werden selber angelegt)
     * @param inifile
     *            Pfad und Name des Inifiles
     * @param split
     *            Trennzeichen
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetreten ist
     **************************************************************************/
    public boolean readIniFile(Hashtable hash, String inifile, char split) {
        int intlinecounter = 0;
        String file = readFile(inifile);
        StringTokenizer lineST = new StringTokenizer(file, System.getProperty("line.separator"));
        while (lineST.hasMoreTokens()) {
            intlinecounter = intlinecounter+1;
            StringTokenizer item = new StringTokenizer(lineST.nextToken(),
                    new Character(split).toString());
            if (!item.hasMoreTokens()) {
                continue;
            }
            String key = item.nextToken();
            //DebugFrame.Msg(10,"key = "+key);
            key.trim();
            if (key.startsWith("#") || key.startsWith("!")|| key.equals("") || key.equals(" ")) {
                continue;
            }
            if (!item.hasMoreTokens()) {
                System.err.println("!!Warning!! Struktur in File " + inifile
                        + " is invalide in Line " + intlinecounter+"!!");
                continue;
            }
            String value = item.nextToken();
            //DebugFrame.Msg(10,"value = "+value);
            //DebugFrame.Msg(10,"Value vor Trim >"+value+"<");
            value.trim();
            //DebugFrame.Msg(10,"Value nach Trim >"+value+"<");
            hash.put(key, value);
        }
        return true;
    }
    /***************************************************************************
     * Liest ein IniFile und schreibt die Daten in einen Hash Das Inifile hat
     * die Form: <br>
     * key value <br>
     * key value <br>
     * usw Als Trennzeichen kann jedes beliebige Zeichen mitgegeben werden.
     * Kommentar mit '#' und '!' wird übersprungen und nicht eingelesen
     * Variablen, die in % Zeichen stehen werden durch etwaigeVariblen, die
     * schon da sind, ersetzt
     *
     * @param hash
     *            Hashtabelle mit allen Schlüsseln (werden selber angelegt)
     * @param inifile
     *            Pfad und Name des Inifiles
     * @param split
     *            Trennzeichen
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetreten ist
     **************************************************************************/
    public boolean readIniFileAndSubstitute(Hashtable hash, String inifile,
                                            char split) {
        String file = readFile(inifile);
        StringTokenizer lineST = new StringTokenizer(file, System.getProperty("line.separator"));
        while (lineST.hasMoreTokens()) {
            String line = lineST.nextToken();
            StringTokenizer item = new StringTokenizer(line, String.valueOf(split));
            if (!item.hasMoreTokens()) {
                continue;
            }
            String key = item.nextToken();
            //DebugFrame.Msg(10,"key = "+key);
            key.trim();
            if (key.startsWith("#") || key.startsWith("!")|| key.equals("")) {
                continue;
            }
            if (!item.hasMoreTokens()) {
                System.err.println("!!Warning!! Struktur in File " + inifile
                        + " is invalide!!");
                continue;
            }
            //String value = item.nextToken();
            String value = line.substring(key.length()+1);
            value.trim();
            hash.put(key, substituteString(hash, value));
        }
        return true;
    }
    /***************************************************************************
     * Schreibt ein Inifile (allgemein) in der Form: <br>
     * key value <br>
     * key value <br>
     * usw... Als Trennzeichen wird immer das Übergebene Zeichenverwendet
     * verwendet Zeilen werden durch \n getrennt
     *
     * @param ini
     *            Hashtable mit Schlüsseln, die geschrieben werden sollen
     * @param inifile
     *            Pfad und Name des Files
     * @param mode
     *            true: File wird an ein bestehendes File angehaengt false:
     *            Datei wird neu geschrieben
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetreten ist
     **************************************************************************/
    public boolean writeIniFile(Hashtable ini, String inifile, char split,
                                boolean mode) {
        String key;
        String value;
        /*
         * char nl = '
         */
        Enumeration keys = ini.keys();
        try {
            FileOutputStream os = new FileOutputStream(inifile, mode);
            while (keys.hasMoreElements()) {
                key = (String) keys.nextElement();
                value = (String) ini.get(key);
                for (int i = 0; i < key.length(); i++) {
                    os.write(key.charAt(i));
                }
                os.write(split);
                for (int i = 0; i < value.length(); i++) {
                    os.write(value.charAt(i));
                }
                //os.write(((String)System.getProperty("line.separator")).charAt(0));
                //os.write(nl);
                /*
                 * os.write('
                 */
                os.write('\n');
            }
            os.close();
        } catch (IOException ioe) {
            System.err.println("****Error! Can not write INIfile****");
            return false;
        }
        return true;
    }
    /***************************************************************************
     * Schreibt den String str in das File.Anschließend wird eine newLine
     * gesetzt
     *
     * @param filename
     *            Name des Files
     * @param str
     *            Der zu schreibende String
     * @param mode
     *            true: String wird angehängt,false: Neue Datei wird erzeugt
     * @return "true" falls alles geklappt hat/ "false" falls Fehler
     *         aufgetretenn ist
     **************************************************************************/
    public boolean writeFile(String filename, String str, boolean mode) {
        try {
            FileOutputStream os = new FileOutputStream(filename, mode);
            for (int i = 0; i < str.length(); i++) {
                os.write(str.charAt(i));
            }
            //os.write(((String)System.getProperty("line.separator")).charAt(0));
            os.write('\n');
            os.close();
        } catch (IOException ioe) {
            System.err.println("****Error! Can not write " + filename + "**** "+ioe);
            return false;
        }
        return true;
    }
    /**
     * Diese Methode schaut in den keys der ersten Hashtable hash nach und trägt
     * alle keys in die Environment Hashtable ein. Sind in der Hashtable 1
     * Konstrukte mit %String% drin, wird nachgeschaut, ob es schon einen Key
     * dieses Namen gibt und ggf. ersetzt
     *
     * @param hash Temporäre Hashtable mit dem eben eingelesenen Environmentfile
     * @param str Der eben eingelesene String der evtl substituirt wird.
     * @return true=alles ok ;false= Fehler
     */
    private String substituteString(Hashtable hash, String str) {
        //DebugFrame.Msg("*************************************");
        String newString = "";
        if (str.startsWith("%")) {
            str = " " + str;
        }
        StringTokenizer st = new StringTokenizer(str, "%");
        if (st.hasMoreTokens()) {
            while (st.hasMoreTokens()) {
                String tok = st.nextToken();
                //DebugFrame.Msg(10,"tok =" +tok);
                newString = newString + tok;
                //DebugFrame.Msg(10,"newString =" +newString);
                if (!st.hasMoreTokens()) {
                    continue;
                }
                String toSubs = st.nextToken();
                //DebugFrame.Msg(10,"toSub ="+toSubs);
                //Schaue nach, ob es diesen String im Environment schon gibt
                if (hash.containsKey(toSubs)) {
                    //Nun muß der key von env in hash eingesetzt werden und
                    // von env entfernt werden
                    newString = newString + (String) hash.get(toSubs);
                    //DebugFrame.Msg(10,"key found - Newstring is now "+newString);
                }
            }
        } else {
            return str;
        }
        //DebugFrame.Msg(10,"++++++++++++++++++++++++++++++++++++++++++++++++++");
        return newString.trim();
    }

    /**
     * Löscht alle Files in einen Verzecihnis
     * @param dirname _Pfad des Verzeichnisses
     * @return hat geklappt
     */
    public boolean emptyDir(String dirname)
    {
        try{
            File dir = new File(dirname);
            File [] allfiles= dir.listFiles();
            //System.err.println ("Anzahl der Files von "+dir.getName()+" = " +allfiles.length);
            for (int i=0; i< allfiles.length;i++)
            {
                /*
                 * Ausnahmen bestätigen die Regel :-)
                 */
                /* Die Ausnahmen aollen nun doch nicht kommen :-(
                 * Aber lassen wir's mal da, man weiss ja nie ;-(
                 */
			/*
			if (        allfiles[i].getName().equals("FrameGeneral.CATSettings")
					|| allfiles[i].getName().equals("DialogPosition.CATSettings"))
			{
				continue;
			}
			*/
                if (! allfiles[i].delete())
                {
                    //DebugFrame.Msg(3,"Kann "+allfiles[i].getName()+" nicht löschen!");
                    return false;
                }
                else

                {
                    //DebugFrame.Msg(3,"Habe "+allfiles[i].getName()+" gelöscht");
                }
            }
        }catch(Exception e){System.err.println("Kann Directory " + dirname + " nicht löschen");}
        return true;
    }
    /**
     * Legt eine "Touchdatei an"
     * @param filename Name der Touchdatei
     * @return hat geklappt (ja/nein)
     */
    public boolean touchfile(String filename)
    {
        File touchfile = new File(filename);
        try
        {
            if (touchfile.createNewFile())
            {
                return true;
            }
            return false;
        }
        catch (IOException ioe )
        {
            System.err.println ("Can not create "+ filename);
            return false;
        }
    }
    public boolean deleteFile(String filename)
    {
        File delfile = new File(filename);
        if (delfile.delete())
        {
            return true;
        }
        return false;
    }
    /**
     * Diese Methode verschiebt das Verzeichnis dirPath nach destPath
     * @param dirPath Quellverzeichnis
     * @param  destPAth Zielverzeichnis
     * @return verschieben hat geklappt ja/nein
     */
    public boolean mvDir(File oldPath, String destPath)
    {
        //String newDirName= oldPath.getName();
        //File newDir = new File(destPath+"\\"+newDirName);
        File newDir = new File(destPath);
        if (! newDir.isDirectory())
        {
            if (! newDir.mkdirs())
            {
                System.err.println("kann "+newDir.getName()+"nicht anlegen");
                return false;
            }
        }
        //System.out.println(destPath+"\\"+newDirName +" angelegt");
        File allFiles[]= oldPath.listFiles();
        for (int i=0;i<allFiles.length;i++)
        {
            if (allFiles[i].isDirectory())
            {
                if(mvDir(allFiles[i],destPath+"\\"+allFiles[i].getName()))
                {
                    allFiles[i].delete();
                }
                else
                {
                    return false;
                }
                //System.out.println("Schiebe Directory "+allFiles[i].getName()+destPath+"\\"+allFiles[i].getName());
            }
            else
            {
                if (! allFiles[i].renameTo(new File(destPath+"\\"+allFiles[i].getName())))
                {
                    System.err.println("Can not delete "+allFiles[i]);
                    return false;
                }
                //System.out.println("Schiebe Datei " +allFiles[i].getName()+destPath+"\\"+allFiles[i].getName());
            }
        }
        if (! oldPath.delete())
        {
            System.err.println("Can not delete "+oldPath);
        }
        return true;
    }
    /**
     * Kopiert ein Verzeichnis von src nach dest
     * @param src Quellverzeichnis
     * @param dest Zielverzeichnis
     * @return geklappt ja/nein
     */
    public boolean copyDir(String src, String dest)
    {
        File newDir = new File(dest);
        File srcFile = new File(src);
        if (srcFile.isFile())
        {
            return copyFile(src,dest);
        }
        if (! newDir.isDirectory())
        {
            if (! newDir.mkdirs())
            {
                System.err.println("kann "+newDir.getName()+"nicht anlegen");
                return false;
            }
        }
        File allFiles[]= srcFile.listFiles();
        if (allFiles==null)
        {
            //new ErrorFrame(src +"doese not exist",false);
            return false;
        }
        for (int i=0;i<allFiles.length;i++)
        {
            if (allFiles[i].isDirectory())
            {
                copyDir(src+"\\"+allFiles[i].getName(),dest+"\\"+allFiles[i].getName());
            }
            else
            {
                copyFile(src+"\\"+allFiles[i].getName(),dest+"\\"+allFiles[i].getName());
            }
        }
        return true;
    }



    /**
     * Kopiert eine Datei in eine Zieldatei
     * @param src Name der Quelldatei
     * @param dest Name der Zieldatei
     * @return geklappt Ja/Nein
     */
    public boolean copyFile(String src, String dest)
    {
        File dat = new File(dest);
        //if (dat==null) System.err.println("NULLFILE");
        if (! dat.getParentFile().isDirectory())
        {
            dat.getParentFile().mkdirs();
            //DebugFrame.Msg("Create "+dest);
        }
        try
        {
            FileInputStream fis = new FileInputStream(src);
            BufferedInputStream bis =new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(dest);
            BufferedOutputStream bos =new BufferedOutputStream(fos);
            byte [] b;
            try{
                b= new byte[bis.available()];
                bis.read(b);
                bos.write(b);
                bis.close();
                bos.close();
            } catch (IOException ioe) {System.err.println("Can not copy "+src+" to "+dest);
                return false;
            }
        } catch (FileNotFoundException fnfe){System.err.println("Can not copy "+src+" to "+dest);
            return false;
        }
        return true;
    }
    /**
     * Schaut nach, wann die Datei zuletz angefasst wurde und gibt den Timestamp zurück
     *
     */
    public String getTimeStamp(String filePath)
    {
        File dat = new File(filePath);
        return new Long(dat.lastModified()).toString();
    }
    /**
     * Test-Suite
     * @param args
     */
    public static void main(String args[]) {
        FileHandling fh = new FileHandling();
        String ini_file;
        if (args.length > 0) {
            ini_file = args[0];
        } else {
            ini_file = "C:\\tmp\\test_ini_file.txt";
        }
        System.out.println("-------------------------------");
        System.out.println("Vector von "+ini_file);
        Vector v = fh.readFileAsVector(ini_file);
        for(int i=0; i<v.size(); i++) {
            System.out.println("line: "+(String)v.elementAt(i));
        }
        System.out.println("-------------------------------");
        System.out.println("Hashtable von "+ini_file);
        Hashtable h = fh.readFileAsHashtable(ini_file);
        Enumeration k = h.keys();
        while(k.hasMoreElements()) {
            Object e = k.nextElement();
            System.out.println("key: >"+e.toString()+"<\tvalue: >"+h.get(e).toString()+"<");
        }
        System.out.println("-----------------END-------------------");
        fh.copyDir("C:\\EC-Apps\\CA-Tools\\catia\\CAA_Appl\\bin","c:\\temp\\neu");
        //fh.mvDir(new File("c:\\temp\\neu"),"C:\\temp\\alt");
    }
}