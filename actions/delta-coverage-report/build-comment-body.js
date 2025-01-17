module.exports = (ctx) => {

    const createCheckRunSummaryText = (checkRun) => {
        const conclusion = checkRun.conclusion === 'success' ? '✅' : '❌';
        return `${conclusion} [${checkRun.viewName}](${checkRun.url})`;
    }

    const buildCoverageInfoMap = (checkRun) => {
        const entitiesRules = checkRun.coverageRules.entitiesRules;
        const coverageMap = new Map();
        for (const [entityName, entityConfig] of Object.entries(entitiesRules)) {
            coverageMap.set(entityName, entityConfig.minCoverageRatio);
        }

        const buildExpectedText = (entity, coverageMap) => {
            const expectedCoverageRatio = coverageMap.get(entity);
            if (expectedCoverageRatio) {
                return `expected ${expectedCoverageRatio * 100}%`;
            } else {
                return '';
            }
        };

        return checkRun.coverageInfo.reduce((acc, it) => {
            const text = `${it.coverageEntity}: ` + [
                buildExpectedText(it.coverageEntity, coverageMap),
                `actual ${it.percents}%`
            ].join(', ');
            acc.set(it.coverageEntity, text);
            return acc;
        }, new Map());
    }

    const buildCheckRunForViewText = (checkRun) => {
        const coverageInfo = buildCoverageInfoMap(checkRun);
        checkRun.verifications.forEach((it) => {
            coverageInfo.set(it.coverageEntity, `🔴 ${it.violation}`)
        });

        const violations = Array.from(coverageInfo.values())
            .map(it => `   - \`${it}\``)
            .join('\n');
        const checkRunSummary = createCheckRunSummaryText(checkRun);
        return `- ${checkRunSummary} \n${violations}`
    }

    const checkRuns = JSON.parse(ctx.checkRunsContent);
    let summaryBuffer = ctx.core.summary
        .addHeading(ctx.commentTitle, '2')
        .addRaw(ctx.commentMarker, true)
        .addEOL()

    checkRuns.forEach(checkRun => {
        const runText = buildCheckRunForViewText(checkRun);
        summaryBuffer = summaryBuffer.addRaw(runText, true);
    });
    return summaryBuffer.stringify()
};
