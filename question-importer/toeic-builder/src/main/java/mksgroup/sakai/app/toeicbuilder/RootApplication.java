package mksgroup.sakai.app.toeicbuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import mksgroup.sakai.app.toeicbuilder.service.ExamBuilderService;

@SpringBootApplication
public class RootApplication implements CommandLineRunner {

    /** Return error code . */
    private static final int EC_INVALID_ARGS = 1;

    /** For logging. */
    private static Logger LOG = LoggerFactory.getLogger(RootApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(RootApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        LOG.info("TOEICBuilder is starting");
        
        if (args.length != 3) {
            for (int i = 0; i < args.length; ++i) {
                LOG.info("args[{}]: {}", i, args[i]);
            }
            
            usage();
            System.exit(EC_INVALID_ARGS);
        } else {
            // Valid arguments
            String mediaUrl = args[1];
            ExamBuilderService ebService = new ExamBuilderService(args[0], mediaUrl, args[2]);
            ebService.parse();
        }
        
        
    }
    
    /**
     * Guideline.
     */
    private void usage() {
        StringBuffer usageSB = new StringBuffer();
        usageSB.append("The TOEICBuilder parse the folder of TOEIC data to build TOEIC Exam package.");
        usageSB.append("\r\n");
        usageSB.append("java toeic-builder <folder> <urlRoot> <out>");
        usageSB.append("\r\n");
        usageSB.append("<folder>: directory contains the media of TOEIC Exam.");
        usageSB.append("\r\n");
        usageSB.append("<urlRoot>: The URL contains the folder media. It contains / at the end or not.");
        usageSB.append("\r\n");
        usageSB.append("<out>: Output directory contain the TOEIC Exam package.");

        System.out.println(usageSB.toString());
    }
}
