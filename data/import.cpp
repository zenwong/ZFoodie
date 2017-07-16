#include<iostream>
#include<boost/algorithm/string/classification.hpp>
#include<boost/algorithm/string/split.hpp>
#include<string>
#include<fstream>
using namespace std;

int main(){
  ifstream infile("hawkercentre.csv");

  string line;
  vector<string> strs;
  while(getline(infile, line)) {
	boost::split(strs, line, boost::is_any_of(","));
	//cout << strs[0] << ":" << strs[1] << " - " << strs[3] << '\n';
  }

  return 0;
}