class InvalidInputException extends Exception {
    String badInput;
    String whyBad;
    InvalidInputException(String badInput, String whyBad) {
        this.badInput = badInput;
        this.whyBad = whyBad;
    }
}
