package com.mayforever.cerede.protocol;

public abstract class BaseClass implements Base{
	private byte protocol = 0;

        public int getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(int totalSize) {
            this.totalSize = totalSize;
        }
        private int totalSize = 0;
	public byte getProtocol() {
		return protocol;
	}

	public void setProtocol(byte protocol) {
		this.protocol = protocol;
	}
}
