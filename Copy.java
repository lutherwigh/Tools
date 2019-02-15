import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.*;
// import java.nio.file.FileAlreadyExistsException;s
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;

// copy file
class Copy {

    static String[] dirNames = { "drawable-hdpi", "drawable-mdpi", "drawable-xhdpi", "drawable-xxhdpi","drawable-xxxhdpi" };
    // copy file source
    static String srcPath = "D:\\workspace\\srcRes";
    // android project name
    static String projectName = "yinghua";
    // copy file target
    // static String targetPath = "D:\\workspace\\luxi\\" + projectName +
    // "\\app\\src\\main\\res";
    static String targetPath = "D:\\workspace\\luxi\\test";
    // copy start time
    static long startTime;
    // alert file nums
    static int fileCount;

    public static void main(String[] args) {

        startTime = System.currentTimeMillis();
        File src = new File(srcPath);
        File target = new File(targetPath);
        // 1. check file exist
        if (src.exists() && target.exists()) {
            // 2. check src file in right directory
            if (src.isDirectory()) {
                recursiveTraversalFolder(srcPath);
                if (src.exists()) {
                    File[] fileArr = src.listFiles();
                    if (null == fileArr || fileArr.length == 0) {
                        System.out.println("src is empty");
                        return;
                    } else {
                        for (File file : fileArr) {
                            if (file.isDirectory()) {
                                for (String s : dirNames) {
                                    if (file.getName().equals(s)) {
                                        System.out.println("---------------------start copy dir " + s + "------------------------------");
                                        fileCopy(file, target, s);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                System.out.println("cost time " + String.valueOf(System.currentTimeMillis() - startTime));
            } else {
                System.out.println("src is not directory");
                return;
            }
        } else {
            System.out.println("src or target file not exist");
            return;
        }
    }

    // if the same name file exist replace it
    public static void fileCopy(File copyDir, File target, String dirName) {
        File[] copyFiles = copyDir.listFiles();
        File[] targetFiles = target.listFiles();
        Path targetPath = null;
        if (targetFiles == null || targetFiles.length == 0) {
            // create new directory
            Path newDirPath = Paths.get(target.getPath() + "\\" + dirName);

            // try{
            //     Files.createDirectory(newDirPath);
            // }
            // catch(FileAlreadyExistsException e){
            //     System.out.print("dir already exists " + dirName);
            //     e.printStackTrace();
            // }
            // catch(IOException e){
            //     e.printStackTrace();
            // }

            File newFile = newDirPath.toFile();
            if (!newFile.exists()) {
                newFile.mkdir();
                targetPath = newFile.toPath();
            }
        } else {
            for (File file : targetFiles) {
                if (file.getName().equals(dirName)) {
                    System.out.println("targetPath " + file.toPath());
                    targetPath = file.toPath();
                }
            }
            if (targetPath == null) {
                Path newDirPath = Paths.get(target.getPath() + "\\" + dirName);
                File newFile = newDirPath.toFile();
                if (!newFile.exists()) {
                    newFile.mkdir();
                    targetPath = newFile.toPath();
                }
            }
        }

        for (File file : copyFiles) {
            try {
                Path tPath = Paths.get(targetPath.toString() + "\\" + file.getName());
                Files.copy(file.toPath(), tPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("copying " + file.getName());
                // System.out.println("copying " + file.getName() + " from " + file.toPath() + "
                // to " + tPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("total copy files count " + copyFiles.length);
    }

    public void fileChannelCopy(File s, File t) {

        FileInputStream fi = null;

        FileOutputStream fo = null;

        FileChannel in = null;

        FileChannel out = null;

        try {

            fi = new FileInputStream(s);

            fo = new FileOutputStream(t);

            in = fi.getChannel();

            out = fo.getChannel();

            in.transferTo(0, in.size(), out);

        } catch (IOException e) {

            e.printStackTrace();

        } finally {

            try {

                fi.close();

                in.close();

                fo.close();

                out.close();

            } catch (IOException e) {

                e.printStackTrace();

            }

        }

    }

    public static void recursiveTraversalFolder(String path) {
        File folder = new File(path);
        if (folder.exists()) {
            File[] fileArr = folder.listFiles();
            if (null == fileArr || fileArr.length == 0) {
                System.out.println("dir is empty");
                return;
            } else {
                File newDir = null;
                String newName = "";
                String fileName = "";
                File parentPath = new File("");
                for (File file : fileArr) {
                    if (file.isDirectory()) {
                        // System.out.println(file.getName() + " is dir");
                        recursiveTraversalFolder(file.getAbsolutePath());
                    } else {// illlegal character detected
                        boolean hasUppercase = false;
                        boolean hasIllegalChar = false;

                        fileName = file.getName();
                        parentPath = file.getParentFile();

                        // character upcase
                        Pattern pattern = Pattern.compile("[A-Z]");
                        Matcher matcher = pattern.matcher(fileName);
                        while (matcher.find()) {
                            hasUppercase = true;
                            newName = fileName.toLowerCase();
                        }

                        pattern = Pattern.compile("[^_ . a-z 0-9]");
                        matcher = pattern.matcher(fileName);
                        matcher.reset();
                        while (matcher.find()) {
                            hasIllegalChar = true;
                            if (newName.equals("")) {
                                newName = fileName;
                            }

                            newName = newName.replaceAll(escapeExprSpecialWord(matcher.group(0)), "");
                            System.out.println("illeagl " + matcher.group(0));
                            System.out.println("after replace " + newName);
                        }

                        if (hasIllegalChar || hasUppercase) {
                            fileCount++;
                            newDir = new File(parentPath + "/" + newName);
                            file.renameTo(newDir);
                            System.out.println("after alter:" + newName);
                        }
                    }
                }
            }
        } else {
            System.out.println("dir not exist");
        }
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|） 
     * 
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!keyword.equals("")) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

}
