package de.prob.parser.ast.types;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by fabian on 25.05.19.
 */
public class RecordType extends Observable implements BType, Observer {

    private List<String> identifiers;

    private List<BType> subtypes;

    public RecordType(List<String> identifiers, List<BType> subtypes) {
        this.identifiers = identifiers;
        this.subtypes = subtypes;
    }

    public RecordType() {
        this.identifiers = new ArrayList<>();
        this.subtypes = new ArrayList<>();
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public void setSubtypes(List<BType> subtypes) {
        this.subtypes = subtypes;
    }

    @Override
    public BType unify(BType otherType) throws UnificationException {
        if (unifiable(otherType)) {
            if (otherType == this) {
                ((UntypedType) otherType).replaceBy(this);
                return this;
            } else {
                RecordType otherRecordType = (RecordType) otherType;
                otherRecordType.replaceBy(this);
                if(otherRecordType.subtypes.size() != subtypes.size()) {
                    throw new UnificationException();
                }
                for(int i = 0; i < subtypes.size(); i++) {
                    if(!identifiers.get(i).equals(otherRecordType.identifiers.get(i))) {
                        throw new UnificationException();
                    }
                    // unify the sub types
                    this.subtypes.get(i).unify(otherRecordType.subtypes.get(i));
                }

                /*
                 * Note, if the sub type has changed this instance will be
                 * automatically updated. Hence, there is no need to store the
                 * result of the unification.
                 */
                return this;
            }
        } else {
            throw new UnificationException();
        }
    }

    @Override
    public boolean unifiable(BType otherType) {
        if (otherType == this) {
            return true;
        } else if (otherType instanceof UntypedType && !this.contains(otherType)) {
            return true;
        } else if (otherType instanceof RecordType) {
            RecordType recordType = (RecordType) otherType;
            return subtypes.stream().allMatch(recordType::unifiable);
        }
        return false;
    }

    public void replaceBy(BType otherType) {
        /*
         * unregister this instance from the sub type, i.e. it will be no longer
         * updated
         */
        subtypes.forEach(subType -> {
            if (subType instanceof Observable) {
                ((Observable) subType).deleteObserver(this);
            }
        });
        // notify all observers of this, they should point now to the otherType
        this.setChanged();
        this.notifyObservers(otherType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void update(Observable o, Object arg) {
        o.deleteObserver(this);
        setSubtypes((List<BType>) arg);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < subtypes.size(); i++) {
            sb.append(identifiers.get(i));
            sb.append(":");
            sb.append(subtypes.get(i).toString());
            if(i < subtypes.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean isUntyped() {
        return this.subtypes.stream().anyMatch(BType::isUntyped);
    }

    @Override
    public boolean contains(BType other) {
        return this.subtypes.contains(other) || this.subtypes.stream().anyMatch(this::contains);
    }

}
