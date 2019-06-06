namespace QuickSplit.Domain
{
    public class Participant
    {
        public Participant() {}
        public Participant(User user, Purchase purchase)
        {
            User = user;
            UserId = User.Id;
            Purchase = purchase;
            PurchaseId = purchase.Id;
        }

        public int UserId { get; set; }
        public virtual User User { get; set; }
        
        public int PurchaseId { get; set; }
        public virtual Purchase Purchase { get; set; }

        protected bool Equals(Participant other)
        {
            return UserId == other.UserId && PurchaseId == other.PurchaseId;
        }

        public override bool Equals(object obj)
        {
            if (ReferenceEquals(null, obj)) return false;
            if (ReferenceEquals(this, obj)) return true;
            if (obj.GetType() != this.GetType()) return false;
            return Equals((Participant) obj);
        }

        public override int GetHashCode()
        {
            unchecked
            {
                return (UserId * 397) ^ PurchaseId;
            }
        }
    }
}