#include <iostream>
#include <string>
#include <fstream>
#include <vector>
#include <boost/algorithm/string.hpp>
#include <hiredis/hiredis.h>
using namespace std;

int main(){
  ifstream infile("hawkercentre.csv");
	auto ctx = redisConnect("127.0.0.1", 6379);

  string line;
  vector<string> strs;
  while(getline(infile, line)) {
		 boost::split(strs, line, boost::is_any_of(","));
	   cout << strs[0] << ":" << strs[1] << " - " << strs[3] << '\n';
		 auto reply = redisCommand(ctx, "geoadd hawkers %s %s %s", strs[0].c_str(), strs[1].c_str(), strs[3].c_str());
		 freeReplyObject(reply);
  }

  return 0;
}
