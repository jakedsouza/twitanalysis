function Tweet(data) {
    this.created_at = data.created_at;
    this.from_user_name = data.from_user_name;
    this.location = data.location;
    this.profile_image_url = data.profile_image_url;
    this.profile_image_url_https = data.profile_image_url_https;
    this.sentimentScore = data.sentimentScore;
    this.text = data.text;
    if(this.sentimentScore <-2){
        this.totalSentimentScoreText = "<span class='label label-important'>Very negative ("+this.sentimentScore+")</span>";
    }else if(this.sentimentScore <0  ){
        this.sentimentScoreText = "<span class='label label-important'>Negative("+this.sentimentScore+")</span>";
    }else if(this.sentimentScore == 0){
        this.sentimentScoreText = "<span class='label'>Neutral("+this.sentimentScore+")</span>";
    }else if(this.sentimentScore > 0){
        this.totalSentimentScoreText = "<span class='label label-success'>Positive("+this.sentimentScore+")</span>";
    }else if(this.sentimentScore > 0.5){
        this.sentimentScoreText = "<span class='label label-success'>Very Positive("+this.sentimentScore+")</span>";
    }
//  debugger;
}
// collection of tweets
function Buzz(key, data) {
    this.buzz = key;
    this.tweets = new Array();
    this.totalSentimentScore = 0;
    var count = 0 ;
    for ( var i = 0; i < data.length; i++) {
        this.tweets.push(new Tweet(data[i]));
        this.totalSentimentScore += data[i].sentimentScore;
        count +=1;
    }
    this.totalSentimentScore = this.totalSentimentScore/count;
    if(this.totalSentimentScore <-2){
        this.totalSentimentScoreText = "<span class='label label-important'>Very negative ("+this.totalSentimentScore+")</span>";
    }else if(this.totalSentimentScore <0  ){
        this.totalSentimentScoreText = "<span class='label label-important'>Negative ("+this.totalSentimentScore+")</span>";
    }else if(this.totalSentimentScore == 0){
        this.totalSentimentScoreText = "<span class='label '>Neutral</span>";
    }else if(this.totalSentimentScore > 0){
        this.totalSentimentScoreText = "<span class='label label-success'>Positive ("+this.totalSentimentScore+")</span>";
    }else if(this.totalSentimentScore > 0.5){
        this.totalSentimentScoreText = "<span class='label label-success'>Very Positive ("+this.totalSentimentScore+")</span>";
    }
    debugger;
}

function AppViewModel() {
    var self = this;
    self.address = ko.observable();
    self.buzzList = ko.observableArray();
    self.isLoading = ko.observable(false);
    self.getTwitResults = function() {
        self.isLoading(true);
        self.buzzList.removeAll() ;
        var data = {
            "location" : $("#location").val(),
            "query" : $("#query").val()
        }
        $.getJSON("twittersentimentanalysis", data, function(result) {
            debugger;
            self.isLoading(false);
            self.address(result['myArrayList'][0]['formattedAddress']);
            var data = result['myArrayList'][1]['myHashMap'];
            //var buzzList = new Array();
            for ( var key in data) {
                var buzz = new Buzz(key, data[key]);
                self.buzzList.push(buzz);
            }
        })
    }

}

var model = new AppViewModel();
ko.applyBindings(model);
