package mg.yoan.invoice;

public class InvoiceStatusTotal {
    private double totalPaid;
    private double totalConfirmed;
    private double totalDraft;

    public InvoiceStatusTotal(double totalDraft, double totalConfirmed, double totalPaid) {
        this.totalDraft = totalDraft;
        this.totalConfirmed = totalConfirmed;
        this.totalPaid = totalPaid;
    }


}
