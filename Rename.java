import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Rename{

    static String dir = "D:\\workspace\\srcRes";

    // alert file count
    static int fileCount;

    public static void main(String[] args) throws IOException{
        recursiveTraversalFolder(dir);
        System.out.println("alert file count:" + fileCount);
    }

    public static void recursiveTraversalFolder(String path){
        File folder = new File(path);
        if(folder.exists()){
            File[] fileArr = folder.listFiles();
            if(null == fileArr || fileArr.length == 0){
                System.out.println("dir is empty");
                return;
            }else{
                File newDir = null;
                String newName = "";
                String fileName = "";
                File parentPath = new File("");
                for(File file : fileArr){
                    if(file.isDirectory()){
                        // System.out.println(file.getName() + " is dir");
                        recursiveTraversalFolder(file.getAbsolutePath());
                    }else{// illlegal character detected
                        boolean hasUppercase = false;
                        boolean hasIllegalChar = false;

                        fileName = file.getName();
                        parentPath = file.getParentFile();
                        
                        // character upcase
                        Pattern pattern = Pattern.compile("[A-Z]");
                        Matcher matcher = pattern.matcher(fileName);
                        while(matcher.find()){
                            hasUppercase = true;
                            newName = fileName.toLowerCase();
                        }

                        pattern = Pattern.compile("[^_ . a-z 0-9]");
                        matcher = pattern.matcher(fileName);
                        StringBuffer sb = new StringBuffer();
                        matcher.reset();
                        while(matcher.find()){
                            hasIllegalChar = true;
                            if(newName.equals("")){
                                newName = fileName;
                            }
                            newName = newName.replaceAll("\\" + matcher.group(0), "");
                            System.out.println("illeagl " + matcher.group(0));
                            System.out.println("after replace" + newName);
                        }
                        
                        if(hasIllegalChar || hasUppercase){
                            fileCount ++;
                            newDir = new File(parentPath + "/" + newName);
                            file.renameTo(newDir);
                            System.out.println( "after alter:" + newName);
                        }
                    }
                }
            }
        }else{
            System.out.println("dir not exist");
        }
    }

}