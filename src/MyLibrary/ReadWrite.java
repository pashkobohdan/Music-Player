package MyLibrary;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;


class ReadWriteException extends IOException{
    public ReadWriteException(String name){
        super(name);
    }
}

public class ReadWrite {

    public static boolean isFileExist (String filename){
        return Files.exists(Paths.get(filename));
    }
    public static long lengthFile(String filename){
        try {
            if(isFileExist(filename)) {
                return Files.newByteChannel(Paths.get(filename)).size();
            }
            else {
                return -1;
            }
        }
        catch (IOException e){
            return -1;
        }
    }

    public static String readWithBuffer(String filename) throws ReadWriteException {
        String resultString="";
        String bufStr;
        int count;

        try(ByteChannel fChan= Files.newByteChannel(Paths.get(filename))){
            ByteBuffer mBuf=ByteBuffer.allocate(1024); // standard buffer is 1024 bytes (1 megabyte)
            do{
                count=fChan.read(mBuf);
                if(count!=-1){
                    mBuf.rewind();
                    bufStr=new String(mBuf.array());
                    resultString=resultString+bufStr;
                }
            }while (count!=-1);
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
        if(resultString==""){
            return null;
        }
        return resultString;
    }
    public static String readWithBuffer(String filename,int longBuffer) throws ReadWriteException {
        String resultString="";
        String bufStr;
        int count;

        try(ByteChannel fChan= Files.newByteChannel(Paths.get(filename))){
            ByteBuffer mBuf=ByteBuffer.allocate(longBuffer); // custom buffer is longBuffer (parameter of function) bytes
            do{
                count=fChan.read(mBuf);
                if(count!=-1){
                    mBuf.rewind();
                    bufStr=new String(mBuf.array());
                    resultString=resultString+bufStr;
                }
            }while (count!=-1);
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
        if(resultString==""){
            return null;
        }
        return resultString;
    }
    public static String readWithMaxBuffer(String filename) throws ReadWriteException {
        String resultString="";

        try(FileChannel fChan= (FileChannel)Files.newByteChannel(Paths.get(filename))){
            if(fChan.size() < 1){
                return null;
            }
            byte[] arg=new byte[(int)fChan.size()];
            MappedByteBuffer mBuf=fChan.map(FileChannel.MapMode.READ_ONLY, 0, fChan.size());
            mBuf.get(arg,0,(int)fChan.size());
            resultString=new String(arg);


            fChan.close();
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
        if(resultString == ""){
            return null;
        }
        return resultString;
    }

    public static void writeWithBuffer(String filename,String data)throws ReadWriteException {
        try(FileChannel fChan=(FileChannel)Files.newByteChannel(Paths.get(filename),
                StandardOpenOption.WRITE,
                StandardOpenOption.READ,
                StandardOpenOption.CREATE))
        {
            MappedByteBuffer mBuf=fChan.map(FileChannel.MapMode.READ_WRITE,0,data.length());
            mBuf.put(data.getBytes());
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
    }
    public static void writeWithMaxBuffer(String filename,String data)throws ReadWriteException {
        try(FileChannel fChan=(FileChannel)Files.newByteChannel(Paths.get(filename),
                StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.READ))
        {
            ByteBuffer mBuf=ByteBuffer.allocate(data.length());
            mBuf.put(data.getBytes());
            fChan.write(mBuf);
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
    }

    public static void appendWithBuffer(String filename,String data)throws ReadWriteException {
        try(FileChannel fChan=(FileChannel)Files.newByteChannel(Paths.get(filename),
                StandardOpenOption.WRITE,StandardOpenOption.READ,StandardOpenOption.CREATE)){

            ByteBuffer bBuf=ByteBuffer.allocate((int)fChan.size());
            fChan.read(bBuf);
            bBuf.rewind();

            MappedByteBuffer mBuf=fChan.map(FileChannel.MapMode.READ_WRITE, 0, data.length() + (int) fChan.size());
            mBuf.put(bBuf.array());
            mBuf.put(data.getBytes());
        }
        catch (InvalidPathException e){
            throw new ReadWriteException("Ошибка указания пути.");
        }
        catch (IOException e){
            throw new ReadWriteException("Ошибка ввода-вывода.");
        }
    }

    public static String[] readToArray(String filename, String token)throws ReadWriteException {
        String result =readWithMaxBuffer(filename);
        if(result == null){
            return null;
        }
        else{
            return result.split(token);
        }
    }
}
