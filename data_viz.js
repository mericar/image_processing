class ColorDistributionGraph {
    constructor(svgElement) {
        this.svg = d3.select(svgElement);
        this.margin = { top: 20, right: 20, bottom: 30, left: 40 };
        this.width = +this.svg.attr("width") - this.margin.left - this.margin.right;
        this.height = +this.svg.attr("height") - this.margin.top - this.margin.bottom;
    }

    setupAxes(data) {
        this.x = d3.scaleBand().rangeRound([0, this.width]).padding(0.1);
        this.y = d3.scaleLinear().rangeRound([this.height, 0]);

        this.x.domain(Object.keys(data));
        this.y.domain([0, d3.max(Object.values(data))]);

        this.g = this.svg.append("g")
            .attr("transform", `translate(${this.margin.left},${this.margin.top})`);

        this.g.append("g")
            .attr("transform", `translate(0,${this.height})`)
            .call(d3.axisBottom(this.x));

        this.g.append("g")
            .call(d3.axisLeft(this.y))
            .append("text")
            .attr("fill", "#ccc")
            .attr("transform", "rotate(-90)")
            .attr("y", 6)
            .attr("dy", "0.71em")
            .attr("text-anchor", "end")
            .text("Frequency");
    }

    drawBars(data) {
        this.g.selectAll(".bar")
            .data(Object.entries(data))
            .enter().append("rect")
            .attr("class", "bar")
            .attr("x", d => this.x(d[0]))
            .attr("y", d => this.y(d[1]))
            .attr("width", this.x.bandwidth())
            .attr("height", d => this.height - this.y(d[1]))
            .attr("fill", d => `#${d[0]}`);  // This ensures each bar's color matches its respective color code
    }

    drawGraph(data) {
        const topN = 200;  // You can adjust this as needed.
        const sortedData = Object.entries(data)
            .sort((a, b) => b[1] - a[1])
            .slice(0, topN)
            .reduce((obj, [key, value]) => ({...obj, [key]: value}), {});

        this.setupAxes(sortedData);
        this.drawBars(sortedData);
    }
}

// Fetch the data and then initialize and draw the graph.
d3.json("img_data2.json").then(data => {
    const graph = new ColorDistributionGraph("svg");
    graph.drawGraph(data);
});
