package src.cacheBlock;

import src.AST.BaseASTNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class cBlock {
    byte[] typeHash = new byte[4];
    byte[] dataHash = new byte[64];
    byte[] par = new byte[4];

    public cBlock() {}

    public cBlock(BaseASTNode node, String dataHash, int p) {
        setTypeHash(node.getClass().hashCode());
        setDataHash(dataHash);
        setPar(p);
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

    public void writeToStream(DataOutputStream dos) throws IOException {
        dos.write(this.typeHash);
        dos.write(this.dataHash);
        dos.write(this.par);
    }

    public static cBlock readFromStream(DataInputStream dis) throws IOException {
        cBlock block = new cBlock();
        dis.readFully(block.typeHash);
        dis.readFully(block.dataHash);
        dis.readFully(block.par);
        return block;
    }
}
