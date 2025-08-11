import java.util.LinkedList;

public class Composition {
    LinkedList<CantusFirmusNode> cantusFirmus;
    LinkedList<FirstSpeciesNode> firstSpecies;
    LinkedList<SecondSpeciesNode> secondSpecies;
    LinkedList<ThirdSpeciesNode> thirdSpecies;
    LinkedList<FourthSpeciesNode> fourthSpecies;

    Composition(LinkedList<CantusFirmusNode> cantusFirmus, LinkedList<FirstSpeciesNode> firstSpecies,
                LinkedList<SecondSpeciesNode> secondSpecies, LinkedList<ThirdSpeciesNode> thirdSpecies,
                LinkedList<FourthSpeciesNode> fourthSpecies) {
        this.cantusFirmus = cantusFirmus;
        this.firstSpecies = firstSpecies;
        this.secondSpecies = secondSpecies;
        this.thirdSpecies = thirdSpecies;
        this.fourthSpecies = fourthSpecies;
    }
    boolean hasCantusFirmus() {
        return (this.cantusFirmus != null);
    }
    boolean hasFirstSpecies() {
        return (this.firstSpecies != null);
    }
    boolean hasSecondSpecies() {
        return (this.secondSpecies != null);
    }

    boolean hasThirdSpecies() {
        return (this.thirdSpecies != null);
    }

    boolean hasFourthSpecies() {
        return (this.fourthSpecies != null);
    }

}
