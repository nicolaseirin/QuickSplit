using MediatR;
using QuickSplit.Application.Groups.Models;
using QuickSplit.Application.Memberships.Models;
using QuickSplit.Application.Users.Models;
using System;
using System.Collections.Generic;
using System.Text;

namespace QuickSplit.Application.Membership.Commands.CreateMembership
{
    public class CreateMembershipCommand : IRequest<MembershipModel>
    {
        public int UserId { get; set; }

        public int GroupId { get; set; }

        public UserModel userModel;

        public GroupModel groupModel;
    }
}
