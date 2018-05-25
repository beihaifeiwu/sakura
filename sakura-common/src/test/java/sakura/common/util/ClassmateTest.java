package sakura.common.util;

import com.fasterxml.classmate.*;
import com.fasterxml.classmate.members.ResolvedConstructor;
import com.fasterxml.classmate.members.ResolvedField;
import com.fasterxml.classmate.members.ResolvedMethod;
import org.junit.Test;
import sakura.common.AbstractTest;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by haomu on 2018/3/29.
 */
public class ClassmateTest extends AbstractTest {

    private TypeResolver typeResolver = new TypeResolver();

    @Test
    public void testType() {

        ResolvedType arrayListType = typeResolver.resolve(ArrayList.class, String.class);
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        ResolvedTypeWithMembers arrayListTypeWithMembers = memberResolver.resolve(arrayListType, null, null);

        // get static methods
        ResolvedMethod[] staticArrayListMethods = arrayListTypeWithMembers.getStaticMethods();
        System.out.println(Arrays.toString(staticArrayListMethods));

        // get instance methods
        ResolvedMethod[] arrayListMethods = arrayListTypeWithMembers.getMemberMethods();
        System.out.println(Arrays.toString(arrayListMethods));

        // get static/instance fields
        ResolvedField[] arrayListFields = arrayListTypeWithMembers.getMemberFields();
        System.out.println(Arrays.toString(arrayListFields));

        // get constructors
        ResolvedConstructor[] arrayListConstructors = arrayListTypeWithMembers.getConstructors();
        System.out.println(Arrays.toString(arrayListConstructors));

    }

    @Test
    public void testAnnotation() {
        ResolvedType someType = typeResolver.resolve(SomeClass.class);
        MemberResolver memberResolver = new MemberResolver(typeResolver);
        memberResolver.setMethodFilter(element -> "someMethod".equals(element.getName()));
        AnnotationConfiguration annConfig = new AnnotationConfiguration.StdConfiguration(AnnotationInclusion.INCLUDE_BUT_DONT_INHERIT);
        ResolvedTypeWithMembers someTypeWithMembers = memberResolver.resolve(someType, annConfig, null);
        ResolvedMethod someMethod = someTypeWithMembers.getMemberMethods()[0];
        Marker marker = someMethod.get(Marker.class);  // marker != null
        System.out.println(marker);
        MarkerA markerA = someMethod.get(MarkerA.class); // markerA != null
        System.out.println(markerA);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Marker { }

    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface MarkerA { }

    public class SomeClass {
        @Marker @MarkerA
        public void someMethod() { }
    }
    public class SomeSubclass extends SomeClass {
        @Override
        public void someMethod() { }
    }

}
