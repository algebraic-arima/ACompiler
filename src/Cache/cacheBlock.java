package src.Cache;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class cacheBlock {
    byte[] typeHash = new byte[4];
    byte[] dataHash = new byte[64];
    byte[] par = new byte[4];
    byte[] symbol = new byte[56];

    public cacheBlock() {
    }

    public cacheBlock(int classHash, String dataHash, int p) {
        setTypeHash(classHash);
        setDataHash(dataHash);
        setPar(p);
    }

    public cacheBlock(int classHash, String dataHash, int p, String s) {
        setTypeHash(classHash);
        setDataHash(dataHash);
        setPar(p);
        setSymbol(s);
    }

    public void setTypeHash(int hash) {
        ByteBuffer.wrap(this.typeHash).putInt(hash);
    }

    public int getTypeHash() {
        return ByteBuffer.wrap(this.typeHash).getInt();
    }

    public void setDataHash(String hash) {
        assert hash.length() == 64;
        System.arraycopy(hash.getBytes(), 0, this.dataHash, 0, 64);
    }

    public String getDataHash() {
        return new String(this.dataHash, 0, 64);
    }

    public void setPar(int index) {
        ByteBuffer.wrap(this.par).putInt(index);
    }

    public int getPar() {
        return ByteBuffer.wrap(this.par).getInt();
    }

    public void setSymbol(String symbol) {
        assert symbol.length() <= 56;
        System.arraycopy(symbol.getBytes(), 0, this.symbol, 0, symbol.length());
        for (int i = symbol.length(); i < 56; i++) {
            this.symbol[i] = 0; // Fill the rest with null bytes
        }
    }

    public String getSymbol() {
        int endIndex = 0;
        while (endIndex < 56 && this.symbol[endIndex] != 0) {
            endIndex++;
        }
        return new String(this.symbol, 0, endIndex);
    }

    public void writeToStream(DataOutputStream dos) throws IOException {
        dos.write(this.typeHash);
        dos.write(this.dataHash);
        dos.write(this.par);
        dos.write(this.symbol);
    }

    public static cacheBlock readFromStream(DataInputStream dis) throws IOException {
        cacheBlock block = new cacheBlock();
        dis.readFully(block.typeHash);
        dis.readFully(block.dataHash);
        dis.readFully(block.par);
        dis.readFully(block.symbol);
        return block;
    }
}
