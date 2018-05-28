
```
    public int toInt(long i) {
        if (i >= Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (i <= Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) i;
    }
```
