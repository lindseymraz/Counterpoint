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

    boolean hasFirstSpecies = (this.firstSpecies != null);
    boolean hasSecondSpecies = (this.secondSpecies != null);

    boolean hasThirdSpecies = (this.thirdSpecies != null);


    boolean hasFourthSpecies = (this.fourthSpecies != null);

}
