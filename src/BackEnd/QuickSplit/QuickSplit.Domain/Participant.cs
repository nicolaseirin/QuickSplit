namespace QuickSplit.Domain
{
    public class Participant
    {
        public int UserId { get; set; }
        public User User { get; set; }
        
        public int GroupId { get; set; }
        public Group Group { get; set; }

        protected bool Equals(Participant other)
        {
            return UserId == other.UserId && GroupId == other.GroupId;
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
                return (UserId * 397) ^ GroupId;
            }
        }
    }
}