package com.github.davityle.ngprocessor;

import com.github.davityle.ngprocessor.util.Option;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.Collections;

import static com.google.common.truth.Truth.ASSERT;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static javax.tools.StandardLocation.SOURCE_OUTPUT;

/**
 * Created by tyler on 6/16/15.
 */
public class CaseTest {

    @Test
    public void testDifferentCase(){
        String content = "" +
                "package com.yella;\n" +
                "import com.ngandroid.lib.annotations.NgModel;\n" +
                "import com.ngandroid.lib.annotations.NgScope;\n\n" +
                "@NgScope(name=\"CaseTest\")\n" +
                "public class CaseTest {\n" +
                "    @NgModel\n" +
                "    CaseMatters cafe;        \n" +
                "}\n" +
                "\n" +
                "class CaseMatters {\n" +
                "    private int case_doesntmatter;\n" +
                "    void setCase_DoesntMATTER(int x){}\n" +
                "    int getCase_DOESntMatteR(){return case_doesntmatter;}\n" +
                "}";
        ASSERT.about(javaSource())
                .that(JavaFileObjects.forSourceString("com.yella.CaseTest", content))
                .processedWith(Collections.singletonList(new NgProcessor(Option.of("ng-processor/src/test/resources/case_layouts"))))
                .compilesWithoutError()
                .and()
                .generatesFileNamed(SOURCE_OUTPUT, "com.yella", "CaseTest$$NgScope.java")
                .and()
                .generatesFileNamed(SOURCE_OUTPUT, "com.yella", "CaseMatters$$NgModel.java");
    }
}
