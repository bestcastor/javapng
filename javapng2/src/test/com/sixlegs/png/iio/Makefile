JAVAPNG=/home/dimitri/Data/Uni/y4s2/elec4707/javapng2
BUILD=$(JAVAPNG)/build/test
DIST=$(JAVAPNG)/dist
A=$(CLASSPATH)
B=$(A):$(DIST)/javapng-2.0-rc2-ext.jar
C=$(B):$(DIST)/javapng-2.0-rc2-iio.jar
D=$(C):$(DIST)/javapng-2.0-rc2.jar
CP=$(D):$(BUILD)

all: *.java
	ant compile-test -find

run: all
	cd $(BUILD); java -cp $(CP) com.sixlegs.png.iio.TestsMain
