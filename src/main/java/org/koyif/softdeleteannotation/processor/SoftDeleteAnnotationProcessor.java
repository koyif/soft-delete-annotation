package org.koyif.softdeleteannotation.processor;

import com.google.auto.service.AutoService;
import org.hibernate.annotations.Where;
import org.koyif.softdeleteannotation.constant.SoftDeleteConstant;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.tools.Diagnostic;
import java.util.Set;

@SupportedAnnotationTypes({"org.koyif.softdeleteannotation.annotation.SoftDelete"})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class SoftDeleteAnnotationProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(annotation);

            annotatedElements.stream()
                    .filter(element -> element.getAnnotation(MappedSuperclass.class) == null)
                    .forEach(this::checkAnnotationWhere);
        }

        return true;
    }

    private void checkAnnotationWhere(Element element) {
        String className = ((TypeElement) element).getQualifiedName().toString();
        if (element.getKind() != ElementKind.CLASS || element.getAnnotation(Entity.class) == null) {
            messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING,
                    "Annotation '@SoftDelete' allowed to class annotated '@Entity' only: " + className);
            return;
        }

        Where whereAnnotation = element.getAnnotation(Where.class);
        if (whereAnnotation == null ||
                !SoftDeleteConstant.SOFT_DELETED_CLAUSE.equalsIgnoreCase(whereAnnotation.clause())) {
            messager.printMessage(Diagnostic.Kind.ERROR, String
                    .format("Entity '%s' should be annotated with '@Where(clause = SoftDeleteConstant.SOFT_DELETED_CLAUSE)'", className));
        }
    }
}
