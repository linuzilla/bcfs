package ncu.cc.commons.utils;

/**
 * @author Jiann-Ching Liu (saber@g.ncu.edu.tw)
 * @version 1.0
 * @since 1.0
 */
public class CJKCharacter {
    public static class Range {
        final int from;
        final int to;

        public Range(int from, int to) {
            this.from = from;
            this.to = to;
        }

        public int getFrom() {
            return from;
        }

        public int getTo() {
            return to;
        }
    }

    public static class Block {
        final String name;
        final Range[]ranges;

        public Block(String name, Range[] ranges) {
            this.name = name;
            this.ranges = ranges;
        }

        public String getName() {
            return name;
        }

        public Range[] getRanges() {
            return ranges;
        }
    }

    public static final Block[] CJK_BLOCKS = new Block[] {
            new Block("CJK Unified Ideographs block.", new Range[] {
                    new Range(0x4e00, 0x62ef),
                    new Range(0x6300, 0x77ff),
                    new Range(0x7800, 0x8cff),
                    new Range(0x8d00, 0x9fcc)
            }),
            new Block("CJKUI Ext A_RECORD block.", new Range[] {
                    new Range(0x3400, 0x4db5)
            }),
            new Block("CJKUI Ext B block.", new Range[] {
                    new Range(0x20000, 0x215ff),
                    new Range(0x21600, 0x230ff),
                    new Range(0x23100, 0x245ff),
                    new Range(0x24600, 0x260ff),
                    new Range(0x26100, 0x275ff),
                    new Range(0x27600, 0x290ff),
                    new Range(0x29100, 0x2a6df)
            }),
            new Block("CJKUI Ext C block.", new Range[] {
                    new Range(0x2a700, 0x2b734)
            }),
            new Block("CJKUI Ext D block.", new Range[] {
                    new Range(0x2b740, 0x2b81d)
            })
    };
}
