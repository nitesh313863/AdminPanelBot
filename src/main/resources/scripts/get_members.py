# import sys
# import json
# import asyncio
# import random
# from telethon.sync import TelegramClient
# from telethon.tl.types import ChannelParticipantsAdmins
#
#
# # ✅ Check if group_chat_id is provided via CLI
# if len(sys.argv) > 1:
#     try:
#         group_chat_id = int(sys.argv[1])  # Get group ID from command line
#     except ValueError:
#         print(json.dumps({"error": "❌ Invalid group ID format"}))
#         sys.exit(1)
# else:
#     print(json.dumps({"error": "❌ No group ID provided"}))
#     sys.exit(1)
#
# # ✅ API Credentials - Replace with your bot token
# API_ID = '26395329'
# API_HASH = '780f9dd42272080669cd02ad12769e09'
# PHONE_NUMBER = '+917024515807'
# BOT_TOKEN = ''  # Use your bot token here
#
#
# # Create Telegram client with bot token session
# client = TelegramClient('bot_session', API_ID, API_HASH).start(bot_token=BOT_TOKEN)
#
# # ✅ Define Rate Limit Parameters
# MAX_REQUESTS_PER_MINUTE = 20  # Limit requests to 20 per minute (example)
# DELAY_BETWEEN_CALLS = 3  # Minimum delay of 3 seconds between requests
#
#
# async def safe_delay():
#     """Add random delay between 1 to 3 seconds to prevent rate limits."""
#     await asyncio.sleep(random.uniform(1, 3))  # Random delay to avoid detection
#
#
# async def fetch_group_members(chat_id):
#     try:
#         # Get the group entity
#         group = await client.get_entity(chat_id)
#
#         # ✅ Fetch all admins using iter_participants with filter
#         admin_ids = []
#         async for participant in client.iter_participants(group, filter=ChannelParticipantsAdmins):
#             admin_ids.append(participant.id)
#
#         # Fetch all group members with delay to avoid rate limits
#         members = []
#         request_count = 0
#
#         async for participant in client.iter_participants(group):
#             # Add delay between API calls
#             await safe_delay()
#
#             # ✅ Determine if the participant is an admin
#             is_admin = participant.id in admin_ids
#
#             # Add member to the list
#             members.append({
#                 "user_id": participant.id,
#                 "first_name": participant.first_name,
#                 "last_name": participant.last_name,
#                 "username": participant.username,
#          #       "bot": participant.bot if hasattr(participant, "bot") else False,
#                 "admin": is_admin
#             })
#
#             # Increment request count and check rate limits
#             request_count += 1
#             if request_count >= MAX_REQUESTS_PER_MINUTE:
#                 print("⏸️ Pausing to avoid rate limits...")
#                 await asyncio.sleep(60)  # Wait for 60 seconds after 20 requests
#                 request_count = 0
#
#         # ✅ Include group name, total member count, and members in JSON response
#         group_name = group.title  # Get the group name
#         total_members = len(members)  # Get total member count
#
#         # Return the response with group name, total member count, and members
#         response = {
#             "group_name": group_name,
#             "total_members": total_members,
#             "members": members
#         }
#
#         print(json.dumps(response))  # Send JSON to Java
#
#     except Exception as e:
#         print(json.dumps({"error": str(e)}))
#
#
# # ✅ Run and fetch members safely
# with client:
#     client.loop.run_until_complete(fetch_group_members(group_chat_id))
