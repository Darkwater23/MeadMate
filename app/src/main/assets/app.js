function calculateAbv(initialGravity, subsequentGravity)
{
    // ABV = (OG - FG) * 131.25

    if(isNaN(parseFloat(initialGravity)))
    {
        return "Initial Gravity value is invalid.";
    }

    if(isNaN(parseFloat(subsequentGravity)))
    {
        return "Gravity value is invalid."
    }

    var ig = new Decimal(initialGravity);
    var sg = new Decimal(subsequentGravity);

    var result = ig.minus(sg).times('131.25');

    return 'ABV ' + result.toFixed(2) + '%';
}