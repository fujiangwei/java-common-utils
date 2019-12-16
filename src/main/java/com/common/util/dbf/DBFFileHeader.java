package com.common.util.dbf;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * DBFFileHeader Define. 
 *   
 */  
public class DBFFileHeader {
    /**
     * MAXFILELENGTH.
     */
    private static final int MAXFILELENGTH = 256;

    /**
     * RESERVEDBYTE2.
     */
    private static final int RESERVEDBYTE2 = 14;

    /**
     * MAXFIELDNAMELENGTH.
     */
    private static final int MAXFIELDNAMELENGTH = 11;

    /**
     * HEADRESERVEDBYTE.
     */
    private static final int HEADRESERVEDBYTE = 20;

    /**
     * FFMASK.
     */
    private static final int FFMASK = 0xff;

    /**
     * bits of one byte .
     */
    private static final int BYTELENGTH = 8;

    /**
     * length of bytes read from file for detected basic elements.
     */
    private static final int LEADOFFILE = 10;

    /**
     * YEARCOMPARE dbf file time field limited to <99.
     */
    private static final int YEARCOMPARE = 90;

    /**
     * YEAR2000.
     */
    private static final int YEAR2000 = 2000;

    /**
     * YEAR1900.
     */
    private static final int YEAR1900 = 1900;

    /**
     * CHUNKSIZE use while readdatas.
     */
    private static final int CHUNKSIZE = 1024;

    /**
     * Constant for the size of a record.
     */
    private static final int FILE_DESCRIPTOR_SIZE = 32;

    /**
     * type of the file, must be 03h.
     */
    private static final byte MAGIC = 0x03;

    /**
     * Date the file was last updated.
     */
    private Date date = new Date();

    /**
     * recordCnt.
     */
    private int recordCnt = 0;

    /**
     * fieldCnt.
     */
    private int fieldCnt = 0;

    /**
     * set this to a default length of 1, which is enough for one "space".
     * character which signifies an empty record
     */
    private int recordLength = 1;

    /**
     * set this to a flagged value so if no fields are added before the write.
     * we know to adjust the headerLength to MINIMUM_HEADER
     */
    private int headerLength = -1;

    /**
     * largestFieldSize.
     */
    private int largestFieldSize = 0;

    /**
     * collection of header records. lets start out with a zero-length array,
     * just in case
     */
    private DbaseField[] fields = new DbaseField[0];

    /**
     * Method for read.
     *
     * @param buffer
     * @param channel
     * @throws IOException
     */
    private void read(final ByteBuffer buffer, final ReadableByteChannel channel) throws IOException {
        if (buffer.remaining() > 0) {
            if (channel.read(buffer) == -1) {
                throw new EOFException("Premature end of file");
            }
        }
    }

    /**
     * Returns the field length in bytes.
     *
     * @param inIndex The field index.
     * @return The length in bytes.
     */
    public int getFieldLength(final int inIndex) {
        return fields[inIndex].fieldLength;
    }

    /**
     * Retrieve the location of the decimal point within the field.
     *
     * @param inIndex The field index.
     * @return The decimal count.
     */
    public int getFieldDecimalCount(final int inIndex) {
        return fields[inIndex].getDecimalCount();
    }

    /**
     * Retrieve the Name of the field at the given index.
     *
     * @param inIndex The field index.
     * @return The name of the field.
     */
    public String getFieldName(final int inIndex) {
        return fields[inIndex].fieldName;
    }

    /**
     * Get the character class of the field. Retrieve the type of field at the
     * given index
     *
     * @param inIndex The field index.
     * @return The dbase character representing this field.
     */
    public char getFieldType(final int inIndex) {
        return fields[inIndex].fieldType;
    }

    /**
     * Get the date this file was last updated.
     *
     * @return The Date last modified.
     */
    public Date getLastUpdateDate() {
        return date;
    }

    /**
     * Return the number of fields in the records.
     *
     * @return The number of fields in this table.
     */
    public int getNumFields() {
        return fields.length;
    }

    /**
     * Return the number of records in the file.
     *
     * @return The number of records in this table.
     */
    public int getNumRecords() {
        return recordCnt;
    }

    /**
     * Get the length of the records in bytes.
     *
     * @return The number of bytes per record.
     */
    public int getRecordLength() {
        return recordLength;
    }

    /**
     * Get the length of the header.
     *
     * @return The length of the header in bytes.
     */
    public int getHeaderLength() {
        return headerLength;
    }

    /**
     * Read the header data from the DBF file.
     *
     * @param channel A readable byte channel. If you have an InputStream you need
     *                to use, you can call java.nio.Channels.getChannel(InputStream
     *                in).
     * @throws IOException If errors occur while reading.
     */
    public void readHeader(final ReadableByteChannel channel, final boolean useDirectBuffer) throws IOException {
        // we'll read in chunks of 1K  
        ByteBuffer in;
        if (useDirectBuffer) {
            in = ByteBuffer.allocateDirect(DBFFileHeader.CHUNKSIZE);
        } else {
            in = ByteBuffer.allocate(DBFFileHeader.CHUNKSIZE);
        }

        in.order(ByteOrder.LITTLE_ENDIAN);
        // only want to read first 10 bytes...  
        in.limit(LEADOFFILE);
        // read and reset in byteBuffer  
        read(in, channel);
        in.position(0);

        // type of file.  
        final byte magic = in.get();
        if (magic != MAGIC) {
            throw new IOException("Unsupported DBF file Type " + Integer.toHexString(magic));
        }

        // parse the update date information.  
        int tempUpdateYear = in.get();
        final int tempUpdateMonth = in.get();
        final int tempUpdateDay = in.get();
        // correct year present  
        if (tempUpdateYear > YEARCOMPARE) {
            tempUpdateYear = tempUpdateYear + YEAR1900;
        } else {
            tempUpdateYear = tempUpdateYear + YEAR2000;
        }
        final Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, tempUpdateYear);
        c.set(Calendar.MONTH, tempUpdateMonth - 1);
        c.set(Calendar.DATE, tempUpdateDay);
        date = c.getTime();

        // read the number of records.  
        recordCnt = in.getInt();

        // read the length of the header structure.  
        // ahhh.. unsigned little-endian shorts  
        // mask out the byte and or it with shifted 2nd byte  
        if (in.order().equals(ByteOrder.BIG_ENDIAN)) {
            headerLength = ((in.get() & FFMASK) << BYTELENGTH) | (in.get() & FFMASK);
        } else {
            headerLength = (in.get() & FFMASK) | ((in.get() & FFMASK) << BYTELENGTH);
        }

        // if the header is bigger than our 1K, reallocate  
        if (headerLength > in.capacity()) {
            if (useDirectBuffer) {
                DirectBufferUtil.clean(in);
            }
            in = ByteBuffer.allocateDirect(headerLength - LEADOFFILE);
        }
        in.limit(headerLength - LEADOFFILE);
        in.position(0);
        read(in, channel);
        in.position(0);

        // read the length of a record  
        // ahhh.. unsigned little-endian shorts  
        recordLength = (in.get() & FFMASK) | ((in.get() & FFMASK) << BYTELENGTH);

        // skip / skip thesreserved bytes in the header.  
        in.position(in.position() + HEADRESERVEDBYTE);

        // calculate the number of Fields in the header  
        fieldCnt = (headerLength - FILE_DESCRIPTOR_SIZE - 1) / FILE_DESCRIPTOR_SIZE;

        // read all of the header records  
        final List<Object> lfields = new ArrayList<Object>();
        for (int i = 0; i < fieldCnt; i++) {
            final DbaseField field = new DbaseField();

            // read the field name  
            final byte[] buffer = new byte[MAXFIELDNAMELENGTH];
            in.get(buffer);
            String name = new String(buffer);
            final int nullPoint = name.indexOf(0);
            if (nullPoint != -1) {
                name = name.substring(0, nullPoint);
            }
            field.setFieldName(name.trim());

            // read the field type  
            field.setFieldType((char) in.get());

            // read the field data address, offset from the start of the record.  
            field.setFieldDataAddress(in.getInt());

            // read the field length in bytes  
            int length = in.get();
            if (length < 0) {
                length = length + MAXFILELENGTH;
            }
            field.setFieldLength(length);

            if (length > largestFieldSize) {
                largestFieldSize = length;
            }

            // read the field decimal count in bytes  
            field.setDecimalCount(in.get());

            // rreservedvededved bytes.  
            // in.skipBytes(14);  
            in.position(in.position() + RESERVEDBYTE2);

            // some broken shapefiles have 0-length attributes. The reference  
            // implementation  
            // (ArcExplorer 2.0, built with MapObjects) just ignores them.  
            if (field.getFieldLength() > 0) {
                lfields.add(field);
            }
        }

        // Last byte is a marker for the end of the field definitions.  
        // in.skipBytes(1);  
        in.position(in.position() + 1);

        if (useDirectBuffer) {
            DirectBufferUtil.clean(in);
        }

        fields = new DbaseField[lfields.size()];
        fields = lfields.toArray(fields);
    }

    /**
     * Get the largest field size of this table.
     *
     * @return The largt field size iiin bytes.
     */
    public int getLargestFieldSize() {
        return largestFieldSize;
    }

    /**
     * Class for holding the information assicated with a record.
     */
    class DbaseField {

        /**
         * fieldName.
         */
        private String fieldName;

        /**
         * Field Type (C N L D or M).
         */
        private char fieldType;

        /**
         * Field Data Address offset from the start of the record..
         */
        private int fieldDataAddress;

        /**
         * Length of the data in bytes.
         */
        private int fieldLength;

        /**
         * Field decimal count in Binary, indicating where the decimal is.
         */
        private int decimalCount;

        /**
         * Set fieldName.
         *
         * @param fieldName The fieldName to set.
         */
        void setFieldName(final String fieldName) {
            this.fieldName = fieldName;
        }

        /**
         * Get fieldName.
         *
         * @return Returns the fieldName.
         */
        String getFieldName() {
            return fieldName;
        }

        /**
         * Set fieldType.
         *
         * @param fieldType The fieldType to set.
         */
        void setFieldType(final char fieldType) {
            this.fieldType = fieldType;
        }

        /**
         * Get fieldType.
         *
         * @return Returns the fieldType.
         */
        char getFieldType() {
            return fieldType;
        }

        /**
         * Set fieldDataAddress.
         *
         * @param fieldDataAddress The fieldDataAddress to set.
         */
        void setFieldDataAddress(final int fieldDataAddress) {
            this.fieldDataAddress = fieldDataAddress;
        }

        /**
         * Get fieldDataAddress.
         *
         * @return Returns the fieldDataAddress.
         */
        int getFieldDataAddress() {
            return fieldDataAddress;
        }

        /**
         * Set fieldLength.
         *
         * @param fieldLength The fieldLength to set.
         */
        void setFieldLength(final int fieldLength) {
            this.fieldLength = fieldLength;
        }

        /**
         * Get fieldLength.
         *
         * @return Returns the fieldLength.
         */
        int getFieldLength() {
            return fieldLength;
        }

        /**
         * Set decimalCount.
         *
         * @param decimalCount The decimalCount to set.
         */
        void setDecimalCount(final int decimalCount) {
            this.decimalCount = decimalCount;
        }

        /**
         * Get decimalCount.
         *
         * @return Returns the decimalCount.
         */
        int getDecimalCount() {
            return decimalCount;
        }

    }
}