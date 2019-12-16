package com.common.util.dbf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Calendar;

public class DBFFileReader {
    /** Buffer Size. */  
    private static final int EKBYTESIZE = 8 * 1024;  
  
    /** DBF File Header . */  
    private DBFFileHeader header;  
  
    /** Data Input Buffer. */  
    private ByteBuffer buffer;
  
    /** File relative channel. */  
    private ReadableByteChannel channel;
  
    /** use for read datas in dbf. */  
    private CharBuffer charBuffer;
  
    /** decoder. */  
    private CharsetDecoder decoder;
  
    /** fieldTypes. */  
    private char[] fieldTypes;  
  
    /** fieldLengths. */  
    private int[] fieldLengths;  
  
    /** ready counts. */  
    private int cnt = 1;  
  
    /** current read row , if not read calls this may be empty. */  
    private Row row;  
  
    /** whether use memoryMap. */  
    private boolean useMemoryMappedBuffer;  
  
    /** randomAccessEnabled. */  
    // private final boolean randomAccessEnabled;  
    /** current dataBuffer Offset. */  
    private int currentOffset = 0;  
  
    /** 
     * Construct for DBFFileReader.java. 
     *  
     * @param channel 
     *            dbfFile channel. 
     * @param useDirectBuffer 
     *            where use useDirectBuffer , if file is not to big to 
     *            handler use false maybe more faster . 
     * @throws IOException 
     */  
    public DBFFileReader(final ReadableByteChannel channel, final boolean useDirectBuffer) throws IOException {
        this.channel = channel;  
        this.useMemoryMappedBuffer = useDirectBuffer;  
        // this.randomAccessEnabled = (channel instanceof FileChannel);  
        header = new DBFFileHeader();  
        header.readHeader(channel, useDirectBuffer);  
        init();  
    }  
  
    /** 
     * Prepare buffer and charbuffer for further read. 
     *  
     * @throws IOException 
     */  
    private void init() throws IOException {  
        // create the ByteBuffer  
        // if we have a FileChannel, lets map it  
        if ((channel instanceof FileChannel) && this.useMemoryMappedBuffer) {  
            final FileChannel fc = (FileChannel) channel;
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());  
            buffer.position((int) fc.position());  
            this.currentOffset = 0;  
        } else {  
            // Force useMemoryMappedBuffer to false  
            this.useMemoryMappedBuffer = false;  
            // Some other type of channel  
            // start with a 8K buffer, should be more than adequate  
            int size = EKBYTESIZE;  
            // if for some reason its not, resize it  
            if (header.getRecordLength() > size) {  
                size = header.getRecordLength();  
            }  
            buffer = ByteBuffer.allocate(size);  
            // fill it and reset  
            fill(buffer, channel);  
            buffer.flip();  
            this.currentOffset = header.getHeaderLength();  
        }  
  
        // The entire file is in little endian  
        buffer.order(ByteOrder.LITTLE_ENDIAN);
  
        // Set up some buffers and lookups for efficiency  
        fieldTypes = new char[header.getNumFields()];  
        fieldLengths = new int[header.getNumFields()];  
        for (int i = 0, ii = header.getNumFields(); i < ii; i++) {  
            fieldTypes[i] = header.getFieldType(i);  
            fieldLengths[i] = header.getFieldLength(i);  
        }  
  
        charBuffer = CharBuffer.allocate(header.getRecordLength());  
        final Charset chars = Charset.forName("ISO-8859-1");
        // Charset chars = Charset.forName("gbk");  
        decoder = chars.newDecoder();  
        row = new Row();  
    }  
  
    /** 
     * Get current row data. Call this right after Row.read() is invoke; 
     *  
     * @return 
     * @throws IOException 
     */  
    public Row readRow() throws IOException {  
        read();  
        return row;  
    }  
  
    /** 
     * Method for read. 
     *  
     * @throws IOException 
     */  
    private void read() throws IOException {  
        boolean foundRecord = false;  
        while (!foundRecord) {  
            // if data is load in batch , we should adjust buffer  
            bufferCheck();  
            charBuffer.position(0);  
            buffer.limit(buffer.position() + header.getRecordLength());  
            decoder.decode(buffer, charBuffer, true);  
            buffer.limit(buffer.capacity());  
            charBuffer.flip();  
            foundRecord = true;  
        }  
        cnt++;  
    }  
  
    /** 
     * Adjust buffer and reload data if necessary. 
     *  
     * @throws IOException 
     */  
    private void bufferCheck() throws IOException {  
        // remaining is less than record length  
        // compact the remaining data and read again  
        if (!buffer.isReadOnly() && (buffer.remaining() < header.getRecordLength())) {  
            this.currentOffset += buffer.position();  
            buffer.compact();  
            fill(buffer, channel);  
            buffer.position(0);  
        }  
    }  
  
    /** 
     * fill buffer with data in channel. 
     *  
     * @param buffer 
     * @param channel 
     * @return 
     * @throws IOException 
     */  
    protected int fill(final ByteBuffer buffer, final ReadableByteChannel channel) throws IOException {  
        int r = buffer.remaining();  
        // channel reads return -1 when EOF or other error  
        // because they a non-blocking reads, 0 is a valid return value!!  
        while ((buffer.remaining() > 0) && (r != -1)) {  
            r = channel.read(buffer);  
        }  
        if (r == -1) {  
            buffer.limit(buffer.position());  
        }  
        return r;  
    }  
  
    /** 
     * Close reader. 
     * @throws IOException 
     */  
    public void close() throws IOException {  
        if (channel.isOpen()) {  
            channel.close();  
        }  
        if (buffer instanceof MappedByteBuffer) {
            DirectBufferUtil.clean(buffer);  
        }  
  
        buffer = null;  
        channel = null;  
        charBuffer = null;  
        decoder = null;  
        header = null;  
        row = null;  
    }  
  
    /** 
     * Method for getHeader. 
     *  
     * @return 
     */  
    public DBFFileHeader getHeader() {  
        return this.header;  
    }  
  
    /** 
     * Query the reader as to whether there is another record. 
     *  
     * @return True if more records exist, false otherwise. 
     */  
    public boolean hasNext() {  
        return cnt < header.getNumRecords() + 1;  
    }  
  
    /** 
     * Represent a Row in dbf file. 
     * @author 2008-3-6 下午01:51:51 
     * 
     */  
    public final class Row {  
        /** 
         * Read a row. 
         * @param column 
         * @return 
         * @throws IOException 
         */  
        public Object read(final int column) throws IOException {  
            final int offset = getOffset(column);  
            return readObject(offset, column);  
        }  
  
        /** 
         * Method for getOffset. 
         *  
         * @param column 
         * @return 
         */  
        private int getOffset(final int column) {  
            int offset = 1;  
            for (int i = 0, ii = column; i < ii; i++) {  
                offset += fieldLengths[i];  
            }  
            return offset;  
        }  
  
        /** 
         * (non-Javadoc).    
         * @see java.lang.Object#toString()  
         * @return . 
         */  
        @Override  
        public String toString() {  
            final StringBuffer ret = new StringBuffer("DBF Row - ");  
            for (int i = 0; i < header.getNumFields(); i++) {  
                ret.append(header.getFieldName(i)).append(": \"");  
                try {  
                    ret.append(this.read(i));  
                } catch (final IOException ioe) {  
                    ret.append(ioe.getMessage());  
                }  
                ret.append("\" ");  
            }  
            return ret.toString();  
        }  
  
        /** 
         * Read a file object. 
         * @param fieldOffset 
         * @param fieldNum 
         * @return 
         * @throws IOException 
         */  
        private Object readObject(final int fieldOffset, final int fieldNum) throws IOException {  
            final char type = fieldTypes[fieldNum];  
            final int fieldLen = fieldLengths[fieldNum];  
            Object object = null;  
            if (fieldLen > 0) {  
                switch (type) {  
                // (L)logical (T,t,F,f,Y,y,N,n)  
                case 'l':  
                case 'L':  
                    switch (charBuffer.charAt(fieldOffset)) {  
                    case 't':  
                    case 'T':  
                    case 'Y':  
                    case 'y':  
                        object = Boolean.TRUE;  
                        break;  
                    case 'f':  
                    case 'F':  
                    case 'N':  
                    case 'n':  
                        object = Boolean.FALSE;  
                        break;  
                    default:  
                        throw new IOException("Unknown logical value : '" + charBuffer.charAt(fieldOffset) + "'");  
                    }  
                    break;  
                // (C)character (String)  
                case 'c':  
                case 'C':  
                    // oh, this seems like a lot of work to parse strings...but,  
                    // For some reason if zero characters ( (int) char == 0 )  
                    // are  
                    // allowed  
                    // in these strings, they do not compare correctly later on  
                    // down  
                    final int start = fieldOffset;  
                    final int end = fieldOffset + fieldLen - 1;  
                    // set up the new indexes for start and end  
                    charBuffer.position(start).limit(end + 1);  
                    final String s = new String(charBuffer.toString().getBytes("ISO-8859-1"), "gbk");  
                    // this resets the limit...  
                    charBuffer.clear();  
                    object = s;  
                    break;  
                // (D)date (Date)  
                case 'd':  
                case 'D':  
                    try {  
                        String tempString = charBuffer.subSequence(fieldOffset, fieldOffset + 4).toString();  
                        final int tempYear = Integer.parseInt(tempString);  
                        tempString = charBuffer.subSequence(fieldOffset + 4, fieldOffset + 6).toString();  
                        final int tempMonth = Integer.parseInt(tempString) - 1;  
                        tempString = charBuffer.subSequence(fieldOffset + 6, fieldOffset + 8).toString();  
                        final int tempDay = Integer.parseInt(tempString);  
                        final Calendar cal = Calendar.getInstance();
                        cal.clear();  
                        cal.set(Calendar.YEAR, tempYear);  
                        cal.set(Calendar.MONTH, tempMonth);  
                        cal.set(Calendar.DAY_OF_MONTH, tempDay);  
                        object = cal.getTime();  
                    } catch (final NumberFormatException nfe) {  
                        // todo: use progresslistener, this isn't a grave error.  
                    }  
                    break;  
                // (F)floating (Double)  
                case 'n':  
                case 'N':  
                    try {  
                        if (header.getFieldDecimalCount(fieldNum) == 0) {  
                            object = new Integer(extractNumberString(charBuffer, fieldOffset, fieldLen));  
                            break;  
                        }  
                        // else will fall through to the floating point number  
                    } catch (final NumberFormatException e) {  
  
                        // Lets try parsing a long instead...  
                        try {  
                            object = new Long(extractNumberString(charBuffer, fieldOffset, fieldLen));  
                            break;  
                        } catch (final NumberFormatException e2) {  
                              
                        }  
                    }  
                case 'f':  
                case 'F': // floating point number  
                    try {  
  
                        object = new Double(extractNumberString(charBuffer, fieldOffset, fieldLen));  
                    } catch (final NumberFormatException e) {  
                        // okay, now whatever we got was truly undigestable.  
                        // Lets go  
                        // with  
                        // a zero Double.  
                        object = new Double(0.0);  
                    }  
                    break;  
                default:  
                    throw new IOException("Invalid field type : " + type);  
                }  
  
            }  
            return object;  
        }  
  
        /** 
         * @param charBuffer2 
         *            TODO 
         * @param fieldOffset 
         * @param fieldLen 
         */  
        private String extractNumberString(final CharBuffer charBuffer2, final int fieldOffset, final int fieldLen) {  
            final String thing = charBuffer2.subSequence(fieldOffset, fieldOffset + fieldLen).toString().trim();  
            return thing;  
        }  
    }  
  
} 