package io.github.notenoughupdates.moulconfig.xml;

import io.github.notenoughupdates.moulconfig.observer.GetSetter;
import lombok.Value;
import lombok.var;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Value
public class TypeMorphismChain {
    List<ParametricTypeMorphism> transformations;
    List<Type> domains;

    TypeMorphismChain(List<ParametricTypeMorphism> transformations, List<Type> domains) {
        this.transformations = transformations;
        this.domains = domains;
        assert domains.size() == transformations.size() + 1;
    }
    public static TypeMorphismChain id(Type type) {
        return new TypeMorphismChain(Collections.emptyList(), Collections.singletonList(type));
    }

    public Type getDomain() {
        return domains.get(0);
    }

    public Type getCoDomain() {
        return domains.get(domains.size() - 1);
    }

    public Optional<TypeMorphismChain> tryExtendWith(ParametricTypeMorphism morphism) {
        var nextCoDomain = morphism.codomain(getCoDomain());
        if (!nextCoDomain.isPresent()) return Optional.empty();
        var newTransformations = new ArrayList<ParametricTypeMorphism>(transformations.size() + 1);
        newTransformations.addAll(transformations);
        newTransformations.add(morphism);
        var newDomains = new ArrayList<Type>(domains.size() + 1);
        newDomains.addAll(domains);
        newDomains.add(nextCoDomain.get());
        return Optional.of(new TypeMorphismChain(newTransformations, newDomains));
    }


    public GetSetter<?> map(GetSetter<?> element) {
        var p = element;
        for (int i = 0; i < transformations.size(); i++) {
            var trans = transformations.get(i);
            var domain = domains.get(i);
            p = trans.apply(domain, p);
        }
        return p;
    }
}
