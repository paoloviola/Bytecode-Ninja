package bytecodeninja.test.project;

import bytecodeninja.project.NinjaModule;
import bytecodeninja.project.NinjaProject;
import bytecodeninja.project.ProjectException;
import bytecodeninja.project.RunConfig;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class ProjectSerializationTesting
{

    public static void main(String[] args) {
        { // Saving
            NinjaProject project = new NinjaProject("Test Project", "C:/Ninja Project");
            NinjaModule module = new NinjaModule(project, "Test Module");
            module.getRunConfigs().add(new RunConfig("Test Config0"));
            module.getRunConfigs().add(new RunConfig("Test Config1"));
            module.getRunConfigs().add(new RunConfig("Test Config2"));
            module.getLibraries().addAll(Arrays.asList("brr", "skrr"));
            project.getModules().add(module);

            System.out.println(project.save() ? "Saving successful!" : "Saving failed!");
        }

        try { // Loading
            NinjaProject.load(new File("C:/Ninja Project"));
            System.out.println("Loading successful!");
        }
        catch (ProjectException | IOException e) {
            System.out.println("Loading failed!");
            e.printStackTrace();
        }
    }

}
