﻿using System;

namespace QuickSplit.Domain
{
   public class Membership
   {
        private int userId;
        private int groupId;
        public User User { get; set; }
        public Group Group { get; set; }

        public int UserId
        {
            get => userId;
            set
            {
                userId = value;
            }
        }

        public int GroupId
        {
            get => groupId;
            set
            {
                groupId = value;
            }
        }

        public Membership()
        {

        }
    }
}


