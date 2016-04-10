package br.com.furb.testeJavaCV;

public class HoughLinesVal {

	private int val;
	private int count;

	public HoughLinesVal(int val) {
		this.val = val;
		count = 1;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		if (val == 0) {
			this.val = val;
		} else {
			this.val = (this.val + val) / 2;
		}
		count++;
	}

	public int getCount() {
		return count;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + val;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoughLinesVal other = (HoughLinesVal) obj;

		int dif = Math.abs(val - other.val);
		if (dif > 5)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HoughLinesVal [val=" + val + ", count=" + count + "]";
	}

}
