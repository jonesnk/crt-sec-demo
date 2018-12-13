package com.gdit.demo;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Vector;

public class TestBed {

    private static int 			numberOfRecords = 30000000;
    private static boolean			createTestFile = true;

    public TestBed(){}

    public static void main(String[] args) throws Exception {

        new TestBed().run();
    }

    private static void createTestFile()
            throws Exception
    {
        long 			accountNumber = 0;
        List<String> lines = new Vector<String>();

        System.out.println("creating test file...");
        long startTime = System.currentTimeMillis();
        File file = new File("loans.txt");

        while(numberOfRecords > 0)
        {
            // FileUtils.writeStringToFile(file, String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", ++accountNumber, "Capital One", "Merchant", "POINTS", 123, 124, 789, 20151023), true);
            numberOfRecords--;
            lines.add(String.format("%s,%s,%s,%s,%s,%s,%s,%s", ++accountNumber, "Suntrust Bank", "Merchant", "POINTS", 123, 124, 789, 20151023));
            if(lines.size() == 1000000)
            {
                System.out.println("writing at " + numberOfRecords);
                FileUtils.writeLines(file, lines, true);
                lines.clear();
            }
        }

        System.out.println("writing final at " + lines.size());
        FileUtils.writeLines(file, lines, true);
        lines.clear();

        long endTime = System.currentTimeMillis();
        System.out.println("created test file @ {$home}/rewardshistory.txt in " + (endTime - startTime) + "ms");
    }

    private void run()
            throws Exception
    {
        // reuse existing file ?
        System.out.println("running...");
        if(createTestFile) createTestFile();

    }
}

